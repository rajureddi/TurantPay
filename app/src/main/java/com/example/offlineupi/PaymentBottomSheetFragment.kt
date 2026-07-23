package com.example.offlineupi

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class PaymentBottomSheetFragment : BottomSheetDialogFragment() {

    private var vpa: String? = null
    private var currentPin = ""
    private var amountString = ""
    private var listener: PaymentListener? = null

    interface PaymentListener {
        fun onPaymentConfirmed(amount: String, pin: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PaymentListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vpa = arguments?.getString(ARG_VPA)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvPayeeVpa = view.findViewById<TextView>(R.id.tvPayeeVpa)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val btnProceedToPin = view.findViewById<MaterialButton>(R.id.btnProceedToPin)
        val viewFlipper = view.findViewById<ViewFlipper>(R.id.viewFlipper)
        
        val layoutPinDots = view.findViewById<LinearLayout>(R.id.layoutPinDots)
        val layoutKeypad = view.findViewById<androidx.gridlayout.widget.GridLayout>(R.id.layoutKeypad) ?: view.findViewById<android.widget.GridLayout>(R.id.layoutKeypad)
        val btnStartPayment = view.findViewById<MaterialButton>(R.id.btnStartPayment)

        tvPayeeVpa.text = vpa ?: "Unknown VPA"

        // Amount State Logic
        btnProceedToPin.setOnClickListener {
            amountString = etAmount.text.toString()
            if (amountString.isNotEmpty() && amountString.toDoubleOrNull() ?: 0.0 > 0) {
                // Hide keyboard
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(etAmount.windowToken, 0)
                
                // Flip to PIN state
                viewFlipper.showNext()
            } else {
                Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }

        // PIN State Logic
        val dots = layoutPinDots.children.toList()
        
        fun updateDots() {
            for (i in dots.indices) {
                dots[i].isSelected = i < currentPin.length
            }
            if (currentPin.length == 6) {
                btnStartPayment.visibility = View.VISIBLE
            } else {
                btnStartPayment.visibility = View.INVISIBLE
            }
        }

        if (layoutKeypad is android.widget.GridLayout) {
            for (child in layoutKeypad.children) {
                if (child is Button) {
                    child.setOnClickListener {
                        if (currentPin.length < 6) {
                            currentPin += child.text.toString()
                            updateDots()
                        }
                    }
                } else if (child is ImageButton) {
                    child.setOnClickListener {
                        if (currentPin.isNotEmpty()) {
                            currentPin = currentPin.dropLast(1)
                            updateDots()
                        }
                    }
                }
            }
        }

        btnStartPayment.setOnClickListener {
            listener?.onPaymentConfirmed(amountString, currentPin)
            dismiss()
        }
    }

    companion object {
        private const val ARG_VPA = "vpa"

        @JvmStatic
        fun newInstance(vpa: String) =
            PaymentBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_VPA, vpa)
                }
            }
    }
}
