package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.ClearHistoryBinding

class ClearHistoryHolder(
    private val binding: ClearHistoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(onClick: () -> Unit) {
        binding.clearHistoryButton.setOnClickListener {
            onClick()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ClearHistoryHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ClearHistoryBinding.inflate(inflater, parent, false)
            return ClearHistoryHolder(binding)
        }
    }
}