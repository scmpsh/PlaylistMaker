package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton = findViewById<TextView>(R.id.leave_settings)

        onShareButtonClick()
        onSupportMessageButtonClick()
        onLicenseButtonClick()

        backButton.setOnClickListener { finish() }
    }

    private fun onLicenseButtonClick() {
        val licenseButton = findViewById<TextView>(R.id.license_button)
        licenseButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, getString(R.string.license_link).toUri())
            startActivity(intent)
        }
    }

    private fun onSupportMessageButtonClick() {
        val supportButton = findViewById<TextView>(R.id.support_button)
        supportButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                setData(getString(R.string.mailto).toUri())
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            }
            startActivity(intent)
        }
    }

    private fun onShareButtonClick() {
        val shareButton = findViewById<TextView>(R.id.share_button)
        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                setType(getString(R.string.text_plain))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.android_course_link))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.chooser_title)))
        }
    }
}