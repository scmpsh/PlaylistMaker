package com.practicum.playlistmaker.presentation.tracks

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R

class ClearTracksButtonHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val clearButton: Button = itemView.findViewById(R.id.clear_history_button)

    fun bind(onClick: () -> Unit, isVisible: Boolean) {
        clearButton.setOnClickListener {
            onClick()
        }
        clearButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}