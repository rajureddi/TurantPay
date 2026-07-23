package com.turantpay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivLogo = findViewById<ImageView>(R.id.ivLogo)
        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        val tvTagline = findViewById<TextView>(R.id.tvTagline)

        // Animate Logo
        ivLogo.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(OvershootInterpolator())
            .start()

        // Animate Text
        tvAppName.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(300)
            .start()

        tvTagline.animate()
            .translationY(0f)
            .alpha(0.8f)
            .setDuration(800)
            .setStartDelay(500)
            .withEndAction {
                routeToNextScreen()
            }
            .start()
    }

    private fun routeToNextScreen() {
        val prefs = getSharedPreferences("OfflineUPIPrefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)

        val targetActivity = if (isFirstLaunch) {
            OnboardingActivity::class.java
        } else {
            MainActivity::class.java
        }

        startActivity(Intent(this, targetActivity))
        finish()
    }
}