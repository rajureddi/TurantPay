package com.turantpay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var layoutIndicators: LinearLayout
    private lateinit var btnNext: MaterialButton
    private lateinit var btnSkip: MaterialButton
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        layoutIndicators = findViewById(R.id.layoutIndicators)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)

        setupOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                if (position == adapter.itemCount - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Next"
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem + 1 < adapter.itemCount) {
                viewPager.currentItem += 1
            } else {
                showModeSelectionDialog()
            }
        }

        btnSkip.setOnClickListener {
            showModeSelectionDialog()
        }
    }

    private fun setupOnboardingItems() {
        val items = listOf(
            OnboardingItem("Welcome to TurantPay", "The fastest offline payment app.", 0),
            OnboardingItem("Instant Offline Payments", "Pay securely using USSD, no internet required.", 0),
            OnboardingItem("Secure and Fast", "Your data is safe and transactions are lightning fast.", 0)
        )
        adapter = OnboardingAdapter(items)
        viewPager.adapter = adapter
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    android.R.drawable.presence_invisible // simple placeholder, we'll use a tint instead
                )
            )
            // Just use a generic background resource or set color
            indicators[i]?.setBackgroundResource(android.R.drawable.presence_invisible)
            layoutIndicators.addView(indicators[i], layoutParams)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = layoutIndicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setColorFilter(ContextCompat.getColor(this, R.color.accent))
                imageView.setImageResource(android.R.drawable.presence_online) // active
            } else {
                imageView.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary_light))
                imageView.setImageResource(android.R.drawable.presence_invisible) // inactive
            }
        }
    }

    private fun showModeSelectionDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Payment Mode")
            .setMessage("TurantPay offers two ways to transact offline.\n\nAuto Mode: Requires Accessibility permissions to parse USSD responses seamlessly for a better UI.\n\nManual Mode: Uses the standard phone dialer to send USSD codes manually.")
            .setPositiveButton("Auto Mode") { _, _ ->
                saveModeAndFinish("auto")
            }
            .setNegativeButton("Manual Mode") { _, _ ->
                saveModeAndFinish("manual")
            }
            .setCancelable(false)
            .show()
    }

    private fun saveModeAndFinish(mode: String) {
        val prefs = getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("pref_mode", mode)
            putBoolean("is_first_launch", false)
            apply()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
