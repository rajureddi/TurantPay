package com.example.offlineupi

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
            // Open details (to be implemented in Plan 5.3)
        }
        rvHistory.adapter = adapter

        loadHistory()
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

        if (transactionList.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }
}