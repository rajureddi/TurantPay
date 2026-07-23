package com.example.offlineupi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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

        // Quick action click listeners
        view.findViewById<LinearLayout>(R.id.btnSendMobile).setOnClickListener {
            Toast.makeText(context, "Send to Mobile tapped", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<LinearLayout>(R.id.btnSendBank).setOnClickListener {
            Toast.makeText(context, "Send to Bank tapped", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<LinearLayout>(R.id.btnCheckBalance).setOnClickListener {
            Toast.makeText(context, "Check Balance tapped", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<LinearLayout>(R.id.btnMiniStatement).setOnClickListener {
            Toast.makeText(context, "Mini Statement tapped", Toast.LENGTH_SHORT).show()
        }
    }
}
