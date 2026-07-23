package com.example.offlineupi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        val isSuccess = intent.getBooleanExtra("is_success", true)
        val amount = intent.getStringExtra("amount") ?: "0.00"
        val vpa = intent.getStringExtra("vpa") ?: "Unknown"
        val txid = intent.getStringExtra("txid") ?: ("TXN" + System.currentTimeMillis().toString().takeLast(6))
        val time = intent.getStringExtra("time") ?: "Just Now"

        val lottieResult = findViewById<LottieAnimationView>(R.id.lottieResult)
        val tvStatusTitle = findViewById<TextView>(R.id.tvStatusTitle)
        val tvAmount = findViewById<TextView>(R.id.tvAmount)
        val tvPayeeVpa = findViewById<TextView>(R.id.tvPayeeVpa)
        val tvTxId = findViewById<TextView>(R.id.tvTxId)
        val tvTimestamp = findViewById<TextView>(R.id.tvTimestamp)
        val btnShare = findViewById<MaterialButton>(R.id.btnShare)
        val btnDone = findViewById<MaterialButton>(R.id.btnDone)

        tvAmount.text = "₹$amount"
        tvPayeeVpa.text = vpa
        tvTxId.text = txid
        tvTimestamp.text = time

        if (isSuccess) {
            tvStatusTitle.text = "Payment Successful"
            tvStatusTitle.setTextColor(Color.parseColor("#2E7D32"))
            btnDone.text = "Done"
            lottieResult.setAnimation(R.raw.lottie_success)
        } else {
            tvStatusTitle.text = "Payment Failed"
            tvStatusTitle.setTextColor(Color.parseColor("#C62828"))
            btnDone.text = "Try Again"
            lottieResult.setAnimation(R.raw.lottie_success) // Fallback or use cross animation if available
        }

        btnShare.setOnClickListener {
            val shareText = """
                --- TurantPay Receipt ---
                Status: ${if (isSuccess) "Successful" else "Failed"}
                Amount: ₹$amount
                To: $vpa
                Transaction ID: $txid
                Date: $time
                Payment Method: Offline USSD *99#
            """.trimIndent()

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share Receipt Via"))
        }

        btnDone.setOnClickListener {
            finish()
        }
    }
}