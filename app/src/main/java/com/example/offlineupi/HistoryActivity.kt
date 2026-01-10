package com.example.offlineupi

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listView = ListView(this)
        setContentView(listView)
        loadHistory(listView)
    }

    private fun loadHistory(listView: ListView) {
        val prefs = getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        val historyRaw = prefs.getString("history", "") ?: ""
        val entries = historyRaw.split(";").filter { it.isNotEmpty() }.toMutableList()

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, entries) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val text1 = view.findViewById<TextView>(android.R.id.text1)
                val text2 = view.findViewById<TextView>(android.R.id.text2)

                val parts = entries[position].split("|") // Format: Time|VPA|Amount|ID
                text1.text = "₹${parts[2]} Paid to ${parts[1]}"
                text2.text = "${parts[0]} • ID: ${parts[3]}"

                // Tap and Hold to Delete
                view.setOnLongClickListener {
                    showDeleteConfirm(position, entries, listView)
                    true
                }
                return view
            }
        }
        listView.adapter = adapter
    }

    private fun showDeleteConfirm(pos: Int, entries: MutableList<String>, listView: ListView) {
        AlertDialog.Builder(this)
            .setTitle("Delete History?")
            .setMessage("Do you want to remove this transaction record?")
            .setPositiveButton("Delete") { _, _ ->
                entries.removeAt(pos)
                val newData = entries.joinToString(";")
                getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE).edit().putString("history", newData).apply()
                loadHistory(listView) // Refresh
            }.setNegativeButton("Cancel", null).show()
    }
}