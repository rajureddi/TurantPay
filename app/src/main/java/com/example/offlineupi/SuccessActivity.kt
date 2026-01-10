package com.example.offlineupi

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(40, 40, 40, 40)
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"))

        val tick = TextView(this)
        tick.text = "✓"
        tick.textSize = 100f
        tick.setTextColor(Color.parseColor("#2E7D32")) // Success Green

        val status = TextView(this)
        status.text = "Payment Successful"
        status.textSize = 24f
        status.setTypeface(null, Typeface.BOLD)
        status.setTextColor(Color.BLACK)

        val amt = TextView(this)
        amt.text = "₹${intent.getStringExtra("amount")}"
        amt.textSize = 48f
        amt.setPadding(0, 40, 0, 10)
        amt.setTextColor(Color.BLACK)

        val details = TextView(this)
        details.text = "To: ${intent.getStringExtra("vpa")}\n\nID: ${intent.getStringExtra("txid")}\n${intent.getStringExtra("time")}"
        details.gravity = Gravity.CENTER
        details.setPadding(0, 20, 0, 0)

        layout.addView(tick)
        layout.addView(status)
        layout.addView(amt)
        layout.addView(details)

        setContentView(layout)
    }
}