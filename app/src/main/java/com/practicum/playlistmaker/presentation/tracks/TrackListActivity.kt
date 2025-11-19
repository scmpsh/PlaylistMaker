package com.practicum.playlistmaker.presentation.tracks

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.TrackListInteractor
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.player.AudioPlayerActivity
import com.practicum.playlistmaker.presentation.player.TRACK_EXTRA

class TrackListActivity : AppCompatActivity() {

    private var inputText: String = DEF_INPUT_TEXT
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var nothingFoundPlaceholder: LinearLayout
    private lateinit var somethingWrongPlaceholder: LinearLayout
    private lateinit var searchHistoryTitle: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var handler: Handler
    private val searchRunnable = Runnable { searchTracks() }
    private lateinit var trackListInteractor: TrackListInteractor
    private var isClickAllowed = true

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
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        handler = Handler(Looper.getMainLooper())
        trackListInteractor = Creator.provideTrackListInteractor(this)

        initViews()
        setupRecyclerView()
        setupSearchInput()
    }

    private fun initViews() {
        val backButton = findViewById<ImageView>(R.id.leave_search)
        val updateButton = findViewById<Button>(R.id.UpdateButton)
        searchHistoryTitle = findViewById(R.id.search_history_title)
        nothingFoundPlaceholder = findViewById(R.id.NothingFoundPlaceholder)
        somethingWrongPlaceholder = findViewById(R.id.SomethingWrongPlaceholder)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)

        backButton.setOnClickListener { finish() }
        updateButton.setOnClickListener { searchTracksDebounce() }
    }

    private fun setupRecyclerView() {
        trackListAdapter = TrackListAdapter(
            mutableListOf(),
            { clearHistory() },
            { track -> onTrackClick(track) },
        )
        recyclerView.adapter = trackListAdapter
    }

    private fun setupSearchInput() {
        val searchInput = findViewById<EditText>(R.id.search_input)
        val clearInputButton = findViewById<ImageView>(R.id.clear_search_button)

        if (inputText.isNotEmpty()) {
            searchInput.setText(inputText)
        }

        searchInput.doOnTextChanged { text, _, _, _ ->
            inputText = text.toString()
            clearInputButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            handleFocus(searchInput)
        }

        searchInput.setOnFocusChangeListener { _, _ ->
            handleFocus(searchInput)
        }

        clearInputButton.setOnClickListener {
            searchInput.text.clear()
            hideKeyboard(searchInput)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun handleFocus(searchInput: EditText) {
        if (searchInput.hasFocus()
            && searchInput.text.isNullOrEmpty()
            && trackListInteractor.getHistory().isNotEmpty()
        ) {
            showHistory()
        } else {
            searchTracksDebounce()
        }
    }

    private fun searchTracksDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks() {
        if (inputText.isNotEmpty()) {

            clearSearchResults()
            progressBar.visibility = View.VISIBLE
            nothingFoundPlaceholder.visibility = View.GONE
            somethingWrongPlaceholder.visibility = View.GONE

            trackListInteractor.searchTracks(
                inputText, object : TrackListInteractor.TrackListConsumer {
                    override fun consume(searchResult: SearchResult) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            when (searchResult) {
                                is SearchResult.Success -> showResults(searchResult.tracks)

                                is SearchResult.Empty -> showNothingFound()

                                is SearchResult.Error -> showError()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun showResults(results: List<Track>) {
        trackListAdapter.setClearButtonVisibility(false)
        trackListAdapter.updateTrackList(results)
        recyclerView.scrollToPosition(0)
        recyclerView.visibility = View.VISIBLE
        searchHistoryTitle.visibility = View.GONE
        nothingFoundPlaceholder.visibility = View.GONE
        somethingWrongPlaceholder.visibility = View.GONE
    }

    private fun showNothingFound() {
        recyclerView.visibility = View.GONE
        nothingFoundPlaceholder.visibility = View.VISIBLE
        somethingWrongPlaceholder.visibility = View.GONE
        searchHistoryTitle.visibility = View.GONE
        trackListAdapter.updateTrackList(emptyList())
    }

    private fun showError() {
        recyclerView.visibility = View.GONE
        nothingFoundPlaceholder.visibility = View.GONE
        somethingWrongPlaceholder.visibility = View.VISIBLE
        searchHistoryTitle.visibility = View.GONE
        trackListAdapter.updateTrackList(emptyList())
    }

    private fun showHistory() {
        val history = trackListInteractor.getHistory()
        if (history.isNotEmpty()) {
            trackListAdapter.updateTrackList(history)
            trackListAdapter.setClearButtonVisibility(true)
            recyclerView.scrollToPosition(0)
            recyclerView.visibility = View.VISIBLE
            searchHistoryTitle.visibility = View.VISIBLE
            nothingFoundPlaceholder.visibility = View.GONE
            somethingWrongPlaceholder.visibility = View.GONE
        }
    }

    private fun clearSearchResults() {
        trackListAdapter.setClearButtonVisibility(false)
        trackListAdapter.updateTrackList(emptyList())
        searchHistoryTitle.visibility = View.GONE
    }

    private fun clearHistory() {
        trackListInteractor.cleanHistory()
        trackListAdapter.updateTrackList(emptyList())
        trackListAdapter.setClearButtonVisibility(false)
        searchHistoryTitle.visibility = View.GONE
    }

    private fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            trackListInteractor.addTrackToHistory(track)

            val displayIntent = Intent(this, AudioPlayerActivity::class.java)
            val bundle = Bundle()

            bundle.putParcelable(TRACK_EXTRA, track)
            displayIntent.putExtras(bundle)
            startActivity(displayIntent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        private const val INPUT_TEXT_KEY = "INPUT_TEXT_KEY"
        private const val DEF_INPUT_TEXT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}