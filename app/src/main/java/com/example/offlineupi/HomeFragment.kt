package com.example.offlineupi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.zxing.integration.android.IntentIntegrator

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. QR Scanner Logic
        view.findViewById<MaterialCardView>(R.id.btnScanQR).setOnClickListener {
            startQrScanner()
        }

        // 2. Mobile Number Logic - Corrected Listener
        val mobileCard = view.findViewById<MaterialCardView>(R.id.btnSendMobile)
        mobileCard.setOnClickListener {
            showMobileInputDialog()
        }

        // 3. Bank IFSC Logic (*99*1*5#)
        view.findViewById<MaterialCardView>(R.id.btnSendBank).setOnClickListener {
            checkPermissionAndDial("*99*1*5#")
        }

        // 4. Check Balance Logic
        view.findViewById<MaterialCardView>(R.id.cardCheckBalance).setOnClickListener {
            checkPermissionAndDial("*99*3#")
        }

        // 5. Mini Statement Logic (*99*6*1#)
        view.findViewById<MaterialCardView>(R.id.cardMiniStatement).setOnClickListener {
            checkPermissionAndDial("*99*6*1#")
        }
    }

    private fun showMobileInputDialog() {
        val safeContext = activity ?: return

        val layout = LinearLayout(safeContext).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 50, 60, 50)
        }

        val phoneInput = EditText(safeContext).apply {
            hint = "10-Digit Mobile Number"
            inputType = InputType.TYPE_CLASS_PHONE
        }

        val amountInput = EditText(safeContext).apply {
            hint = "Enter Amount"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(phoneInput)
        layout.addView(amountInput)

        AlertDialog.Builder(safeContext)
            .setTitle("Direct Pay to Mobile")
            .setView(layout)
            .setCancelable(true)
            .setPositiveButton("Pay Now") { _, _ ->
                val phone = phoneInput.text.toString().trim()
                val amount = amountInput.text.toString().trim()

                if (phone.length == 10 && amount.isNotEmpty()) {
                    // Pre-filled USSD string based on your requirement
                    val directCode = "*99*1*1*$phone*$amount*1#"

                    // Store for history before dialing
                    (activity as? MainActivity)?.setPendingTransaction(phone, amount)

                    checkPermissionAndDial(directCode)
                } else {
                    Toast.makeText(safeContext, "Enter valid 10-digit number and amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startQrScanner() {
        IntentIntegrator.forSupportFragment(this)
            .setPrompt("Scan UPI QR")
            .setOrientationLocked(true)
            .initiateScan()
    }

    private fun checkPermissionAndDial(code: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            dialCode(code)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 101)
        }
    }

    private fun dialCode(code: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:${Uri.encode(code)}")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Dialing failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result?.contents != null) {
            (activity as? MainActivity)?.handleQrResult(result.contents)
        }
    }
}