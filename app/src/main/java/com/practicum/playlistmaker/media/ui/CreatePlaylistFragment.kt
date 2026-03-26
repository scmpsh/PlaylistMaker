package com.practicum.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {

    protected open val viewModel: PlaylistViewModel by viewModel()

    private var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    protected var imagePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    setImage(uri)
                    imagePath = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.coverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.nameInput.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistButton.isEnabled = !text.isNullOrBlank()
        }

        binding.createPlaylistButton.setOnClickListener {
            onSaveClick()
        }

        binding.backButton.setOnClickListener {
            handleBackNavigation()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            })
    }

    protected fun setImage(uri: Uri?) {
        Glide.with(this)
            .load(uri)
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.dimen_8dp))
            )
            .into(binding.coverImage)
    }

    protected open fun onSaveClick() {
        viewModel.addPlaylist(
            binding.nameInput.text?.toString().orEmpty(),
            binding.descriptionInput.text?.toString().orEmpty(),
            imagePath
        )
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_created_message, binding.nameInput.text?.toString()),
            Toast.LENGTH_SHORT
        ).show()
        findNavController().popBackStack()
    }

    protected open fun handleBackNavigation() {
        if (imagePath != null || !binding.nameInput.text.isNullOrBlank() || !binding.descriptionInput.text.isNullOrBlank()) {
            showConfirmExitDialog()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun showConfirmExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creation_dialog_title)
            .setMessage(R.string.finish_playlist_creation_dialog_message)
            .setNegativeButton(R.string.cancel_button_label) { _, _ -> }
            .setPositiveButton(R.string.finish_button_label) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}