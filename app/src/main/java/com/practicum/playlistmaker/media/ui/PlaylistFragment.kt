package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.media.ui.view_model.PlaylistState
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter { playlist ->
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistDetailsFragment,
                PlaylistDetailsFragment.createArgs(playlist.id)
            )
        }
        binding.recyclerView.adapter = adapter

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmpty()
            is PlaylistState.Content -> showContent(state.playlists)
            else -> {}
        }
    }

    private fun showEmpty() {
        binding.recyclerView.isVisible = false
        binding.playlistPlaceholder.isVisible = true
        binding.playlistPlaceholderText.isVisible = true
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.recyclerView.isVisible = true
        binding.playlistPlaceholder.isVisible = false
        binding.playlistPlaceholderText.isVisible = false

        adapter?.updatePlaylists(playlists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}
