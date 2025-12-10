package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            setType(context.getString(R.string.text_plain))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.android_course_link))
        }
        val chooserIntent =
            Intent.createChooser(intent, context.getString(R.string.chooser_title)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(chooserIntent)
    }

    override fun openLink() {
        val intent =
            Intent(Intent.ACTION_VIEW, context.getString(R.string.license_link).toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(intent)
    }

    override fun openEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            setData(context.getString(R.string.mailto).toUri())
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}