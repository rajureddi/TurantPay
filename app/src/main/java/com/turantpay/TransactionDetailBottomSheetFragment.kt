package com.turantpay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class TransactionDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var vpa: String? = null
    private var amount: String? = null
    private var txId: String? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vpa = arguments?.getString(ARG_VPA)
        amount = arguments?.getString(ARG_AMOUNT)
        txId = arguments?.getString(ARG_TXID)
        date = arguments?.getString(ARG_DATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_detail_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvAmount).text = "₹${amount ?: "0.00"}"
        view.findViewById<TextView>(R.id.tvPayeeVpa).text = vpa ?: "Unknown"
        view.findViewById<TextView>(R.id.tvTxId).text = txId ?: "Unknown"
        view.findViewById<TextView>(R.id.tvTimestamp).text = date ?: "Unknown"

        view.findViewById<MaterialButton>(R.id.btnShare).setOnClickListener {
            val shareText = """
                --- TurantPay Receipt ---
                Status: Successful
                Amount: ₹$amount
                To: $vpa
                Transaction ID: $txId
                Date: $date
                Payment Method: Offline USSD *99#
            """.trimIndent()

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share Receipt Via"))
        }

        view.findViewById<MaterialButton>(R.id.btnRepeatPayment).setOnClickListener {
            dismiss()
            (activity as? MainActivity)?.handleQrResult(vpa ?: "")
        }
    }

    companion object {
        private const val ARG_VPA = "vpa"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_TXID = "txid"
        private const val ARG_DATE = "date"

        @JvmStatic
        fun newInstance(item: TransactionModel) =
            TransactionDetailBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_VPA, item.vpa)
                    putString(ARG_AMOUNT, item.amount)
                    putString(ARG_TXID, item.txId)
                    putString(ARG_DATE, item.date)
                }
            }
    }
}
