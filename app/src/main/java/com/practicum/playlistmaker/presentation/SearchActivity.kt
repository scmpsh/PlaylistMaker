package com.practicum.playlistmaker.presentation

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.APP_PREFERENCES
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.TrackPreferences
import com.practicum.playlistmaker.data.api.ItunesApi
import com.practicum.playlistmaker.data.dto.ItunesResponse
import com.practicum.playlistmaker.data.dto.Track
import com.practicum.playlistmaker.presentation.adapter.TrackListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var inputText: String = DEF_INPUT_TEXT
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesApi = retrofit.create(ItunesApi::class.java)

    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var nothingFoundPlaceholder: LinearLayout
    private lateinit var somethingWrongPlaceholder: LinearLayout
    private lateinit var searchHistoryTitle: TextView

    private val trackPreferences = TrackPreferences()

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

        initViews()
        setupRecyclerView()
        setupSearchInput()
    }

    private fun initViews() {
        val backButton = findViewById<TextView>(R.id.leave_search)
        val updateButton = findViewById<Button>(R.id.UpdateButton)
        searchHistoryTitle = findViewById(R.id.search_history_title)
        nothingFoundPlaceholder = findViewById(R.id.NothingFoundPlaceholder)
        somethingWrongPlaceholder = findViewById(R.id.SomethingWrongPlaceholder)
        recyclerView = findViewById(R.id.recyclerView)
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        backButton.setOnClickListener { finish() }
        updateButton.setOnClickListener { getTrackList() }
    }

    private fun setupRecyclerView() {
        trackListAdapter = TrackListAdapter(sharedPreferences, mutableListOf()) {
            clearHistory()
        }
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
            handleFocus(searchInput, text)
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTrackList()
                true
            } else false
        }

        searchInput.setOnFocusChangeListener { _, _ ->
            handleFocus(searchInput, searchInput.text)
        }

        clearInputButton.setOnClickListener {
            searchInput.text.clear()
            showHistory()
            hideKeyboard(searchInput)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun handleFocus(searchInput: EditText, text: CharSequence?) {
        if (searchInput.hasFocus() && text.isNullOrEmpty() &&
            trackPreferences.read(sharedPreferences).isNotEmpty()
        ) {
            showHistory()
        } else {
            showEmptyResults()
        }
    }

    private fun getTrackList() {
        itunesApi.search(inputText).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(
                call: Call<ItunesResponse?>,
                response: Response<ItunesResponse?>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()?.results
                    if (!results.isNullOrEmpty()) {
                        showResults(results)
                    } else {
                        showNothingFound()
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<ItunesResponse?>, t: Throwable) {
                showError()
            }
        })
    }

    private fun showResults(results: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        searchHistoryTitle.visibility = View.GONE
        nothingFoundPlaceholder.visibility = View.GONE
        somethingWrongPlaceholder.visibility = View.GONE
        trackListAdapter.setClearButtonVisibility(false)
        trackListAdapter.updateTrackList(results)
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
        val history = trackPreferences.read(sharedPreferences)
        if (history.isNotEmpty()) {
            trackListAdapter.updateTrackList(history)
            trackListAdapter.setClearButtonVisibility(true)
            recyclerView.visibility = View.VISIBLE
            searchHistoryTitle.visibility = View.VISIBLE
            nothingFoundPlaceholder.visibility = View.GONE
            somethingWrongPlaceholder.visibility = View.GONE
        }
    }

    private fun showEmptyResults() {
        recyclerView.visibility = View.VISIBLE
        trackListAdapter.setClearButtonVisibility(false)
        trackListAdapter.updateTrackList(emptyList())
        searchHistoryTitle.visibility = View.GONE
    }

    private fun clearHistory() {
        trackPreferences.cleanCachedTrackList(sharedPreferences)
        trackListAdapter.updateTrackList(emptyList())
        trackListAdapter.setClearButtonVisibility(false)
        searchHistoryTitle.visibility = View.GONE
    }

    companion object {
        private const val INPUT_TEXT_KEY = "INPUT_TEXT_KEY"
        private const val DEF_INPUT_TEXT = ""
        private const val ITUNES_URL = "https://itunes.apple.com"
    }
}
