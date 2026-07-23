package com.turantpay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MobileInputBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mobile_input_bottom_sheet, container, false)
        
        val etMobile = view.findViewById<TextInputEditText>(R.id.etMobileNumber)
        val btnProceed = view.findViewById<MaterialButton>(R.id.btnProceed)

        btnProceed.setOnClickListener {
            val mobile = etMobile.text.toString()

            if (mobile.length == 10) {
                if (activity is MainActivity) {
                    (activity as MainActivity).openPaymentForMobile(mobile)
                }
                dismiss()
            } else {
                Toast.makeText(context, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
