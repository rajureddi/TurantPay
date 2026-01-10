package com.example.offlineupi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    // Inside SettingsFragment.kt
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Developer Link
        view.findViewById<LinearLayout>(R.id.btnDeveloper).setOnClickListener {
            val developerUrl = "https://github.com/yourusername" // Replace with your actual link
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(developerUrl))
            startActivity(intent)
        }

        // NPCI Helpline
        view.findViewById<LinearLayout>(R.id.btnHelpline).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18001201740"))
            startActivity(intent)
        }
    }
}
