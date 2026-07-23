package com.turantpay

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check for Updates (Manual Trigger)
        view.findViewById<LinearLayout>(R.id.btnCheckUpdates).setOnClickListener {
            (activity as? MainActivity)?.checkForAppUpdates(isManualCheck = true)
        }

        // Developer Link
        view.findViewById<LinearLayout>(R.id.btnDeveloper).setOnClickListener {
            val developerUrl = "https://github.com/rajureddi/turantpay"
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
