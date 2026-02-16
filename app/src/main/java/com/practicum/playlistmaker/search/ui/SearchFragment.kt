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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.player.ui.mapper.toUi
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchAdapter
    private val searchViewModel by viewModel<SearchViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

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

        onTrackClickDebounce = debounce(
            0,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            searchViewModel.onTrackClicked(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_audioPlayerFragment,
                AudioPlayerFragment.createArgs(track.toUi())
            )
        }

        setupRecyclerView()
        setupSearchInput()

        searchViewModel.observeSearchState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.updateButton.setOnClickListener {
            searchViewModel.onSearchTextChanged(
                binding.searchInput.text.toString(),
                true
            )
        }
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
            onTrackClick = { onTrackClickDebounce(it) },
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
                searchViewModel.onSearchTextChanged(binding.searchInput.text.toString())
            }
        }

        binding.clearSearchButton.setOnClickListener {
            hideKeyboard()
            binding.searchInput.text.clear()
            searchViewModel.onSearchTextChanged(binding.searchInput.text.toString())
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
    }
}