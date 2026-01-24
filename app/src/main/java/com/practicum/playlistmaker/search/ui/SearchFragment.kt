package com.practicum.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.player.ui.mapper.toUi
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchAdapter
    private val searchViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchInput()

        searchViewModel.observeSearchState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.updateButton.setOnClickListener { searchViewModel.onRetryClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.items)
            is SearchState.Empty -> showNothingFound()
            is SearchState.Error -> showError()
            SearchState.Loading -> showLoading()
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchAdapter(
            onTrackClick = { openPlayer(it) },
            onClearHistoryClick = { searchViewModel.onClearHistoryClicked() }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearchInput() {
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            if (binding.searchInput.hasFocus()) {
                searchViewModel.onSearchTextChanged(text.toString())
            }
            binding.clearSearchButton.isVisible = !text.isNullOrBlank()
        }

        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInput.text.isNullOrBlank()) {
                searchViewModel.onSearchTextChanged("")
            }
        }

        binding.clearSearchButton.setOnClickListener {
            binding.searchInput.text.clear()
            hideKeyboard()
        }
    }

    private fun openPlayer(track: Track) {
        if (searchViewModel.clickDebounce()) {
            searchViewModel.onTrackClicked(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track.toUi())
            )
        }
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.nothingFoundPlaceholder.isVisible = false
        binding.somethingWrongPlaceholder.isVisible = false
        binding.recyclerView.isVisible = false
        binding.searchHistoryTitle.isVisible = false
    }

    private fun showContent(items: List<SearchUiItem>) = with(binding) {
        adapter.updateTrackList(items)
        progressBar.isVisible = false
        recyclerView.isVisible = true
        nothingFoundPlaceholder.isVisible = false
        somethingWrongPlaceholder.isVisible = false
        binding.searchHistoryTitle.isVisible = items.any { it == SearchUiItem.ClearHistoryItem }
    }

    private fun showNothingFound() {
        adapter.updateTrackList(emptyList())
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.nothingFoundPlaceholder.isVisible = true
        binding.somethingWrongPlaceholder.isVisible = false
        binding.searchHistoryTitle.isVisible = false
    }

    private fun showError() {
        binding.progressBar.isVisible = false
        binding.recyclerView.isVisible = false
        binding.nothingFoundPlaceholder.isVisible = false
        binding.somethingWrongPlaceholder.isVisible = true
        binding.searchHistoryTitle.isVisible = false
        adapter.updateTrackList(emptyList())
    }
}