package com.example.offlineupi

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

// Corrected: Uses fragment_history layout
class HistoryFragment : Fragment(R.layout.fragment_history) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view.findViewById<ListView>(R.id.historyListView)
        loadHistory(listView)
    }

    private fun loadHistory(listView: ListView) {
        val prefs = requireActivity().getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        val rawData = prefs.getString("history", "") ?: ""
        val entries = rawData.split(";").filter { it.isNotEmpty() }.toMutableList()

        // Using simple_list_item_2 to show Name/Amount and Date/ID
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_2, android.R.id.text1, entries) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val v = super.getView(position, convertView, parent)
                val t1 = v.findViewById<TextView>(android.R.id.text1)
                val t2 = v.findViewById<TextView>(android.R.id.text2)

                val data = entries[position].split("|") // Format: Date|VPA|Amount|ID
                if (data.size >= 4) {
                    t1.text = "₹${data[2]} to ${data[1]}"
                    t2.text = "${data[0]} • ID: ${data[3]}"
                }

                // Long press to delete
                v.setOnLongClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete History?")
                        .setMessage("Remove this record?")
                        .setPositiveButton("Delete") { _, _ ->
                            entries.removeAt(position)
                            prefs.edit().putString("history", entries.joinToString(";")).apply()
                            loadHistory(listView)
                        }.setNegativeButton("Cancel", null).show()
                    true
                }
                return v
            }
        }
        listView.adapter = adapter
    }
}