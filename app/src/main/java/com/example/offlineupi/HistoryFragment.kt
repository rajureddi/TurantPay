package com.example.offlineupi

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var rvHistory: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private val transactionList = mutableListOf<TransactionModel>()
    private lateinit var adapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        rvHistory = view.findViewById(R.id.rvHistory)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)

        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(transactionList) { transaction ->
            TransactionDetailBottomSheetFragment.newInstance(transaction)
                .show(childFragmentManager, "TransactionDetailSheet")
        }
        rvHistory.adapter = adapter

        // Swipe-to-delete setup
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val deletedItem = transactionList[position]
                
                transactionList.removeAt(position)
                adapter.notifyItemRemoved(position)
                checkEmptyState()

                Snackbar.make(rvHistory, "Transaction deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        transactionList.add(position, deletedItem)
                        adapter.notifyItemInserted(position)
                        checkEmptyState()
                        saveHistoryToPrefs()
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event != DISMISS_EVENT_ACTION) {
                                saveHistoryToPrefs()
                            }
                        }
                    })
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(rvHistory)

        loadHistory()
    }

    private fun checkEmptyState() {
        if (transactionList.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
        }
    }

    private fun saveHistoryToPrefs() {
        val prefs = requireActivity().getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        val formatted = transactionList.joinToString(";") { "${it.date}|${it.vpa}|${it.amount}|${it.txId}" }
        prefs.edit().putString("history", formatted).apply()
    }

    fun loadHistory() {
        val prefs = requireActivity().getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        val rawData = prefs.getString("history", "") ?: ""
        val entries = rawData.split(";").filter { it.isNotEmpty() }

        transactionList.clear()
        for (entry in entries) {
            val data = entry.split("|") // Date|VPA|Amount|ID
            if (data.size >= 4) {
                transactionList.add(TransactionModel(data[0], data[1], data[2], data[3]))
            }
        }

        checkEmptyState()
        adapter.notifyDataSetChanged()
    }
}