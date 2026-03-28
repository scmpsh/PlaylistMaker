package com.practicum.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(requireArguments().getInt(PLAYLIST_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistHeader.text = getString(R.string.edit_playlist)
        binding.createPlaylistButton.text = getString(R.string.save_button_label)

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.nameInput.setText(playlist.name)
            binding.descriptionInput.setText(playlist.description)

            if (playlist.coverImagePath.isNotEmpty()) {
                val file = File(playlist.coverImagePath)
                if (file.exists()) {
                    setImage(Uri.fromFile(file))
                }
            }
        }
    }

    override fun onSaveClick() {
        viewModel.updatePlaylist(
            binding.nameInput.text?.toString().orEmpty(),
            binding.descriptionInput.text?.toString().orEmpty(),
            imagePath
        )
        findNavController().popBackStack()
    }

    override fun handleBackNavigation() {
        findNavController().popBackStack()
    }

    companion object {
        private const val PLAYLIST_ID = "PLAYLIST_ID"
        private const val PLAYLIST_NAME = "PLAYLIST_NAME"
        private const val PLAYLIST_DESCRIPTION = "PLAYLIST_DESCRIPTION"
        private const val PLAYLIST_COVER_PATH = "PLAYLIST_COVER_PATH"

        fun createArgs(
            id: Int,
            name: String,
            description: String,
            coverPath: String
        ): Bundle = bundleOf(
            PLAYLIST_ID to id,
            PLAYLIST_NAME to name,
            PLAYLIST_DESCRIPTION to description,
            PLAYLIST_COVER_PATH to coverPath
        )
    }
}
