package com.practicum.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.media.ui.view_model.PlaylistDetailsState
import com.practicum.playlistmaker.media.ui.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.player.ui.mapper.toUi
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.sharing.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {
    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(
            requireArguments().getInt(PLAYLIST_ID)
        )
    }

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private var adapter: PlaylistDetailsAdapter? = null
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheets()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        adapter = PlaylistDetailsAdapter(
            onTrackClick = { track ->
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track.toUi())
                )
            },
            onTrackLongClick = { track ->
                showConfirmDeleteTrackDialog(track)
            }
        )
        binding.playlistsRecyclerView.adapter = adapter

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.menuShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.menuEdit.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val state = viewModel.state.value as? PlaylistDetailsState.Content
            if (state != null) {
                findNavController().navigate(
                    R.id.action_playlistDetailsFragment_to_editPlaylistFragment,
                    EditPlaylistFragment.createArgs(
                        state.playlist.id,
                        state.playlist.name,
                        state.playlist.description,
                        state.playlist.coverImagePath
                    )
                )
            }
        }

        binding.menuDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showConfirmDeletePlaylistDialog()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailsState.Content -> render(state)
                is PlaylistDetailsState.Loading -> {
                }

                is PlaylistDetailsState.Deleted -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylistDetails()
    }

    private fun setupBottomSheets() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                _binding?.overlay?.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = (slideOffset + 1f) / 2f
            }
        })
    }

    private fun render(state: PlaylistDetailsState.Content) {
        val playlist = state.playlist

        adapter?.updateTrackList(state.tracks.map { SearchUiItem.TrackItem(it) })

        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.playlistDescription.isVisible = playlist.description.isNotEmpty()

        val totalMinutes = (state.totalDuration / 60000)
        val minutesString =
            resources.getQuantityString(
                R.plurals.minutes_count,
                totalMinutes,
                totalMinutes
            )
        val tracksString = resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        binding.playlistInfo.text =
            getString(R.string.playlist_info_format, minutesString, tracksString)

        val imageUri =
            if (playlist.coverImagePath.isNotEmpty()) Uri.fromFile(File(playlist.coverImagePath)) else null

        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.ic_track_placeholder_312)
            .transform(CenterCrop())
            .into(binding.playlistCover)

        binding.playlistItem.playlistName.text = playlist.name
        binding.playlistItem.tracksCount.text = tracksString
        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.ic_track_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.dimen_2dp))
            )
            .into(binding.playlistItem.cover)

        binding.root.post {
            val location = IntArray(2)
            binding.shareButton.getLocationOnScreen(location)
            val parentLocation = IntArray(2)
            binding.root.getLocationOnScreen(parentLocation)

            val bottomOfButton = location[1] + binding.shareButton.height - parentLocation[1]
            val margin = resources.getDimensionPixelSize(R.dimen.dimen_21dp)
            val topOffset = bottomOfButton + margin
            
            val screenHeight = binding.root.height
            tracksBottomSheetBehavior.peekHeight = screenHeight - topOffset
        }
    }

    private fun sharePlaylist() {
        val state = viewModel.state.value as? PlaylistDetailsState.Content ?: return

        val tracksString = resources.getQuantityString(
            R.plurals.tracks_count,
            state.playlist.tracksCount,
            state.playlist.tracksCount
        )

        val playlistInfoBuilder = StringBuilder().apply {
            append(state.playlist.name).append("\n")
            if (state.playlist.description.isNotEmpty()) {
                append(state.playlist.description).append("\n")
            }
            append(tracksString).append("\n")

            state.tracks.forEachIndexed { index, track ->
                val trackTime =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
                append(
                    getString(
                        R.string.playlist_share_track_format,
                        index + 1,
                        track.artistName,
                        track.trackName,
                        trackTime
                    )
                )
                if (index < state.tracks.size - 1) append("\n")
            }
        }

        viewModel.sharePlaylist(
            getString(R.string.no_tracks_to_share),
            playlistInfoBuilder.toString()
        )
    }

    private fun showConfirmDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
            .setTitle(R.string.confirm_delete_track)
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrack(track)
            }
            .show()
    }

    private fun showConfirmDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialog)
            .setTitle(
                getString(
                    R.string.confirm_delete_playlist_message,
                    binding.playlistItem.playlistName.text
                )
            )
            .setNegativeButton(R.string.no) { _, _ -> }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    companion object {
        private const val PLAYLIST_ID = "PLAYLIST_ID"

        fun createArgs(playlistId: Int): Bundle {
            val bundle = Bundle()
            bundle.putInt(PLAYLIST_ID, playlistId)
            return bundle
        }
    }
}
