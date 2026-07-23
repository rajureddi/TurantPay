package com.turantpay

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
            showMobileNumberDialog()
        }
        view.findViewById<LinearLayout>(R.id.btnSendBank).setOnClickListener {
            (activity as? MainActivity)?.dialUssd("*99*1*5#")
        }
        view.findViewById<LinearLayout>(R.id.btnCheckBalance).setOnClickListener {
            (activity as? MainActivity)?.dialUssd("*99*3#")
        }
        view.findViewById<LinearLayout>(R.id.btnMiniStatement).setOnClickListener {
            (activity as? MainActivity)?.dialUssd("*99*6*1#")
        }

    }

    private fun showMobileNumberDialog(defaultPhone: String = "") {
        val mobileSheet = MobileInputBottomSheetFragment()
        mobileSheet.show(childFragmentManager, "MobileInputSheet")
    }
}
