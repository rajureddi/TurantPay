package com.example.offlineupi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TransactionModel(
    val date: String,
    val vpa: String,
    val amount: String,
    val txId: String,
    val status: String = "SUCCESS"
)

class HistoryAdapter(
    private val transactions: MutableList<TransactionModel>,
    private val onItemClick: (TransactionModel) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatarInitials: TextView = view.findViewById(R.id.tvAvatarInitials)
        val tvPayeeName: TextView = view.findViewById(R.id.tvPayeeName)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvStatusBadge: TextView = view.findViewById(R.id.tvStatusBadge)

        init {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(transactions[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]
        holder.tvPayeeName.text = item.vpa
        holder.tvTimestamp.text = item.date
        holder.tvAmount.text = "₹${item.amount}"
        holder.tvStatusBadge.text = item.status
        holder.tvAvatarInitials.text = item.vpa.firstOrNull()?.toString()?.uppercase() ?: "U"
    }

    override fun getItemCount() = transactions.size
}
