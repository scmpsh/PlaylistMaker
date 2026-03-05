package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.player.ui.mapper.toUi
import com.practicum.playlistmaker.search.ui.SearchAdapter
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.sharing.domain.model.Track
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTrackClickDebouncer()
        setupRecyclerView()

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.items)
            is SearchState.Empty -> showPlaceholder()
            else -> {}
        }
    }

    private fun showPlaceholder() {
        binding.favoriteTracksPlaceholder.isVisible = true
        binding.favoriteTracksPlaceholderText.isVisible = true
        adapter.updateTrackList(emptyList())
    }

    private fun showContent(items: List<SearchUiItem>) {
        binding.favoriteTracksPlaceholder.isVisible = false
        binding.favoriteTracksPlaceholderText.isVisible = false
        binding.favoriteTracksRecyclerView.isVisible = true

        adapter.updateTrackList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTrackClickDebouncer() {
        onTrackClickDebounce = debounce(
            0,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_mediaFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track.toUi())
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchAdapter(onTrackClickDebounce) {}
        binding.favoriteTracksRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance() =
            FavoriteTracksFragment()
    }
}