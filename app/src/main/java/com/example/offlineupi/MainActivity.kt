package com.example.offlineupi

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), PaymentBottomSheetFragment.PaymentListener {

    private var currentVpa: String = ""
    private var currentAmount: String = ""

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Call permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Call permission denied. Dialing via Phone App.", Toast.LENGTH_SHORT).show()
        }
    }

    private val barcodeLauncher = registerForActivityResult(com.journeyapps.barcodescanner.ScanContract()) { result ->
        if (result.contents != null) {
            handleQrResult(result.contents)
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkCallPermission()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabScan = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabScan)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_history -> HistoryFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }

        fabScan.setOnClickListener {
            val options = com.journeyapps.barcodescanner.ScanOptions()
            options.setDesiredBarcodeFormats(com.journeyapps.barcodescanner.ScanOptions.QR_CODE)
            options.setPrompt("")
            options.setCameraId(0)
            options.setBeepEnabled(false)
            options.setBarcodeImageEnabled(true)
            options.setCaptureActivity(CustomCaptureActivity::class.java)
            barcodeLauncher.launch(options)
        }
    }

    private fun checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    fun dialUssd(code: String) {
        val encodedCode = Uri.encode(code)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$encodedCode"))
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encodedCode"))
                startActivity(intent)
            }
        } else {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encodedCode"))
            startActivity(intent)
        }
    }

    fun setPendingTransaction(vpa: String, amount: String) {
        this.currentVpa = vpa
        this.currentAmount = amount
    }

    fun openPaymentForVpa(vpa: String) {
        currentVpa = vpa
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UPI_ID", currentVpa)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "UPI ID Copied: $vpa", Toast.LENGTH_SHORT).show()
        PaymentBottomSheetFragment.newInstance(currentVpa, isMobilePay = false).show(supportFragmentManager, "PaymentSheet")
    }

    fun openPaymentForMobile(phone: String) {
        currentVpa = phone
        PaymentBottomSheetFragment.newInstance(phone, isMobilePay = true).show(supportFragmentManager, "MobilePaymentSheet")
    }

    fun onMobilePaymentConfirmed(phone: String, amount: String) {
        currentVpa = phone
        currentAmount = amount

        // Direct USSD code for Mobile Pay
        val directCode = "*99*1*1*$phone*$amount*1#"
        dialUssd(directCode)
    }

    // Function called after QR Scan (or by barcodeLauncher)
    fun handleQrResult(scannedData: String) {
        currentVpa = if (scannedData.contains("pa=")) {
            scannedData.substringAfter("pa=").substringBefore("&")
        } else {
            scannedData
        }

        // 1. AUTOMATIC COPY TO CLIPBOARD
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UPI_ID", currentVpa)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "UPI ID Copied Automatically", Toast.LENGTH_SHORT).show()

        // 2. Display QR Scan Bottom Sheet without asking for PIN/Amount
        QrScanBottomSheetFragment.newInstance(currentVpa).show(supportFragmentManager, "QrScanSheet")
    }

    override fun onPaymentConfirmed(amount: String, pin: String) {
        currentAmount = amount
        dialUssd("*99*1*3#")
    }

    override fun onResume() {
        super.onResume()
        if (currentVpa.isNotEmpty() && currentAmount.isNotEmpty()) {
            showCompletionDialog()
        }
    }

    private fun showCompletionDialog() {
        val txId = "TXN" + System.currentTimeMillis().toString().takeLast(6)
        val timeStamp = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())

        AlertDialog.Builder(this)
            .setTitle("Confirm Transaction Status")
            .setMessage("Did the bank confirm the transaction for ₹$currentAmount to $currentVpa?")
            .setCancelable(false)
            .setPositiveButton("Yes, Save to History") { _, _ ->
                saveTransactionToHistory(timeStamp, txId)
                
                // Launch Result/Success Activity
                val intent = Intent(this, SuccessActivity::class.java).apply {
                    putExtra("is_success", true)
                    putExtra("amount", currentAmount)
                    putExtra("vpa", currentVpa)
                    putExtra("txid", txId)
                    putExtra("time", timeStamp)
                }
                currentVpa = ""
                currentAmount = ""
                startActivity(intent)
            }
            .setNegativeButton("No / Failed") { _, _ ->
                currentVpa = ""
                currentAmount = ""
            }
            .show()
    }

    private fun saveTransactionToHistory(timeStamp: String, txId: String) {
        val prefs = getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        val existingHistory = prefs.getString("history", "") ?: ""
        val newEntry = "$timeStamp|$currentVpa|$currentAmount|$txId"
        prefs.edit().putString("history", "$newEntry;$existingHistory").apply()
        Toast.makeText(this, "Receipt Saved in History", Toast.LENGTH_SHORT).show()
    }
}