package com.example.offlineupi

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var currentVpa: String = ""
    private var currentAmount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

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
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            true
        }
    }

    // Function called by HomeFragment after QR Scan
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

        // Notify the user it was copied automatically
        Toast.makeText(this, "UPI ID Copied Automatically", Toast.LENGTH_SHORT).show()

        showAmountDialog()
    }

    override fun onResume() {
        super.onResume()
        if (currentVpa.isNotEmpty() && currentAmount.isNotEmpty()) {
            showCompletionDialog()
        }
    }

    private fun showCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Payment")
            .setMessage("Did the bank confirm the transaction for ₹$currentAmount?")
            .setCancelable(false)
            .setPositiveButton("Yes, Save to History") { _, _ ->
                saveTransactionToHistory()
                currentVpa = ""
                currentAmount = ""
            }
            .setNegativeButton("No / Failed") { _, _ ->
                currentVpa = ""
                currentAmount = ""
            }
            .show()
    }

    fun setPendingTransaction(vpa: String, amount: String) {
        this.currentVpa = vpa
        this.currentAmount = amount
    }

    private fun saveTransactionToHistory() {
        val timeStamp = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val txId = "TXN" + System.currentTimeMillis().toString().takeLast(6)

        val prefs = getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        val existingHistory = prefs.getString("history", "") ?: ""
        val newEntry = "$timeStamp|$currentVpa|$currentAmount|$txId"

        prefs.edit().putString("history", "$newEntry;$existingHistory").apply()
        Toast.makeText(this, "Receipt Saved in History", Toast.LENGTH_SHORT).show()
    }

    private fun showAmountDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 40, 60, 40)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val txtTitle = TextView(this).apply {
            text = "Confirm Payment Detail"
            textSize = 20f
            setPadding(0, 0, 0, 20)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val vpaDisplay = TextView(this).apply {
            text = currentVpa
            textSize = 18f
            setTextColor(android.graphics.Color.BLUE)
            setPadding(0, 10, 0, 10)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Manual Re-copy Button
        val btnCopy = Button(this).apply {
            text = "RE-COPY UPI ID"
            setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("UPI_ID", currentVpa)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "UPI ID Copied Again!", Toast.LENGTH_SHORT).show()
            }
        }

        // Updated Instructions
        val instructions = TextView(this).apply {
            text = "\nINSTRUCTION:\nUPI ID is already COPIED.\nJust PASTE it in the next shown screen."
            textSize = 15f
            setTextColor(android.graphics.Color.RED)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 20, 0, 20)
        }

        val amountInput = EditText(this).apply {
            hint = "Enter Amount"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(txtTitle)
        layout.addView(vpaDisplay)
        layout.addView(btnCopy)
        layout.addView(instructions)
        layout.addView(amountInput)

        AlertDialog.Builder(this)
            .setTitle("Merchant Payment")
            .setView(layout)
            .setCancelable(false)
            .setPositiveButton("Start Payment") { _, _ ->
                currentAmount = amountInput.text.toString()
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${Uri.encode("*99*1*3#")}"))
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}