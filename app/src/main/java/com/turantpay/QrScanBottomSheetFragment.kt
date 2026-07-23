package com.turantpay

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class QrScanBottomSheetFragment : BottomSheetDialogFragment() {

    private var vpa: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_scan_bottom_sheet, container, false)

        vpa = arguments?.getString("VPA")

        val tvScannedVpa = view.findViewById<TextView>(R.id.tvScannedVpa)
        tvScannedVpa.text = vpa ?: "Unknown VPA"

        // Auto-copy VPA
        vpa?.let { copyToClipboard(it) }

        val btnRecopyUpi = view.findViewById<MaterialButton>(R.id.btnRecopyUpi)
        btnRecopyUpi.setOnClickListener {
            vpa?.let { copyToClipboard(it) }
            Toast.makeText(context, "UPI ID copied to clipboard again", Toast.LENGTH_SHORT).show()
        }

        val btnStartPaymentUssd = view.findViewById<MaterialButton>(R.id.btnStartPaymentUssd)
        btnStartPaymentUssd.setOnClickListener {
            if (activity is MainActivity) {
                // Dial *99*1*3# for Send to VPA
                (activity as MainActivity).dialUssd("*99*1*3#")
            }
            dismiss()
        }

        return view
    }

    private fun copyToClipboard(text: String) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UPI ID", text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        @JvmStatic
        fun newInstance(vpa: String) =
            QrScanBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString("VPA", vpa)
                }
            }
    }
}
