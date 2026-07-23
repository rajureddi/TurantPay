package com.example.offlineupi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        
        // Time-based greeting logic
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        val greeting = when (timeOfDay) {
            in 5..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            in 17..20 -> "Good Evening,"
            else -> "Good Night,"
        }
        tvGreeting.text = greeting

        // Quick action click listeners with original USSD logic
        view.findViewById<LinearLayout>(R.id.btnSendMobile).setOnClickListener {
            showMobilePayDialog()
        }
        view.findViewById<LinearLayout>(R.id.btnSendBank).setOnClickListener {
            showBankPayOptionDialog()
        }
        view.findViewById<LinearLayout>(R.id.btnCheckBalance).setOnClickListener {
            (activity as? MainActivity)?.dialUssd("*99*3#")
        }
        view.findViewById<LinearLayout>(R.id.btnMiniStatement).setOnClickListener {
            (activity as? MainActivity)?.dialUssd("*99*6*1#")
        }

        // Recent Payees setup
        val rvRecentPayees = view.findViewById<RecyclerView>(R.id.rvRecentPayees)
        rvRecentPayees.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dummyPayees = listOf(
            "Amit" to "9876543210",
            "Rahul" to "9812345678",
            "Priya" to "priya@okicici",
            "Sunil" to "sunil@ybl",
            "Neha" to "neha@paytm"
        )
        rvRecentPayees.adapter = RecentPayeesAdapter(dummyPayees) { payee ->
            if (payee.all { it.isDigit() }) {
                showMobilePayDialog(defaultPhone = payee)
            } else {
                (activity as? MainActivity)?.openPaymentForVpa(payee)
            }
        }

        // Expandable Notes setup
        val layoutNotesHeader = view.findViewById<LinearLayout>(R.id.layoutNotesHeader)
        val tvNotesBody = view.findViewById<TextView>(R.id.tvNotesBody)
        val ivNotesToggle = view.findViewById<ImageView>(R.id.ivNotesToggle)

        layoutNotesHeader.setOnClickListener {
            if (tvNotesBody.visibility == View.GONE) {
                tvNotesBody.visibility = View.VISIBLE
                ivNotesToggle.animate().rotation(-90f).start()
            } else {
                tvNotesBody.visibility = View.GONE
                ivNotesToggle.animate().rotation(90f).start()
            }
        }
    }

    private fun showMobilePayDialog(defaultPhone: String = "") {
        val safeContext = context ?: return

        val layout = LinearLayout(safeContext).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 50, 60, 50)
        }

        val phoneInput = android.widget.EditText(safeContext).apply {
            hint = "10-Digit Mobile Number"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            if (defaultPhone.isNotEmpty()) setText(defaultPhone)
        }

        val amountInput = android.widget.EditText(safeContext).apply {
            hint = "Enter Amount (₹)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(phoneInput)
        layout.addView(amountInput)

        androidx.appcompat.app.AlertDialog.Builder(safeContext)
            .setTitle("Direct Pay to Mobile")
            .setView(layout)
            .setCancelable(true)
            .setPositiveButton("Pay Now") { _, _ ->
                val phone = phoneInput.text.toString().trim()
                val amount = amountInput.text.toString().trim()

                if (phone.length == 10) {
                    if (amount.isNotEmpty()) {
                        // Pre-filled USSD string as per requirement
                        val directCode = "*99*1*1*$phone*$amount*1#"
                        val mainAct = activity as? MainActivity
                        mainAct?.setPendingTransaction(phone, amount)
                        mainAct?.dialUssd(directCode)

                        // Also trigger success receipt view
                        val txId = "TXN" + System.currentTimeMillis().toString().takeLast(6)
                        val timeStamp = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                        val intent = android.content.Intent(context, SuccessActivity::class.java).apply {
                            putExtra("is_success", true)
                            putExtra("amount", amount)
                            putExtra("vpa", phone)
                            putExtra("txid", txId)
                            putExtra("time", timeStamp)
                        }
                        startActivity(intent)
                    } else {
                        (activity as? MainActivity)?.openPaymentForVpa(phone)
                    }
                } else {
                    Toast.makeText(safeContext, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBankPayOptionDialog() {
        val safeContext = context ?: return
        val options = arrayOf("Send via Bank IFSC Code (*99*1*5#)", "Send via UPI ID / VPA")
        
        androidx.appcompat.app.AlertDialog.Builder(safeContext)
            .setTitle("Send to Bank / VPA")
            .setItems(options) { _, which ->
                if (which == 0) {
                    (activity as? MainActivity)?.dialUssd("*99*1*5#")
                } else {
                    showSingleInputDialog("Send via UPI ID", "Enter Payee UPI ID (e.g. name@upi)") { vpa ->
                        (activity as? MainActivity)?.openPaymentForVpa(vpa)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSingleInputDialog(title: String, hintText: String, onSubmit: (String) -> Unit) {
        val context = context ?: return
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(title)

        val input = android.widget.EditText(context).apply {
            hint = hintText
            setPadding(40, 30, 40, 30)
        }
        builder.setView(input)

        builder.setPositiveButton("Proceed") { _, _ ->
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                onSubmit(text)
            } else {
                Toast.makeText(context, "Please enter details", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    inner class RecentPayeesAdapter(
        private val payees: List<Pair<String, String>>,
        private val onPayeeClick: (String) -> Unit
    ) : RecyclerView.Adapter<RecentPayeesAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvInitials: TextView = view.findViewById(R.id.tvInitials)
            val tvName: TextView = view.findViewById(R.id.tvName)

            init {
                view.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        onPayeeClick(payees[adapterPosition].second)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_payee, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (name, vpa) = payees[position]
            holder.tvName.text = name
            holder.tvInitials.text = name.firstOrNull()?.toString()?.uppercase() ?: ""
        }

        override fun getItemCount() = payees.size
    }
}
