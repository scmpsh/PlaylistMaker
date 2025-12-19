package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.AudioPlayerActivity
import com.practicum.playlistmaker.player.ui.TRACK_EXTRA
import com.practicum.playlistmaker.player.ui.mapper.toUi
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var inputText: String = DEF_INPUT_TEXT

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter

    private val searchViewModel by viewModel<SearchViewModel>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT_KEY, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_TEXT_KEY, DEF_INPUT_TEXT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupSearchInput()

        searchViewModel.observeSearchState().observe(this) {
            render(it)
        }

        binding.leaveSearch.setOnClickListener { finish() }
        binding.updateButton.setOnClickListener { searchViewModel.onRetryClicked() }
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
            inputText = text?.toString() ?: DEF_INPUT_TEXT
            searchViewModel.onSearchTextChanged(text.toString())
            binding.clearSearchButton.isVisible = !text.isNullOrBlank()
        }

        binding.searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchViewModel.onSearchTextChanged(inputText)
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
            startActivity(
                Intent(this, AudioPlayerActivity::class.java).apply {
                    putExtra(TRACK_EXTRA, track.toUi())
                }
            )
        }
    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
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

    companion object {
        private const val INPUT_TEXT_KEY = "INPUT_TEXT_KEY"
        private const val DEF_INPUT_TEXT = ""
    }

}