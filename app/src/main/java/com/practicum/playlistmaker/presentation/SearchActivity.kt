package com.practicum.playlistmaker.presentation

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
import com.practicum.playlistmaker.R
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
    private val trackListAdapter = TrackListAdapter()
    private lateinit var nothingFoundPlaceholder: LinearLayout
    private lateinit var somethingWrongPlaceholder: LinearLayout

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

        val backButton = findViewById<TextView>(R.id.leave_search)
        val updateButton = findViewById<Button>(R.id.UpdateButton)
        nothingFoundPlaceholder = findViewById(R.id.NothingFoundPlaceholder)
        somethingWrongPlaceholder = findViewById(R.id.SomethingWrongPlaceholder)

        backButton.setOnClickListener { finish() }
        updateButton.setOnClickListener {
            getTrackList()
        }

        onSearchText()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = trackListAdapter
    }

    private fun getTrackList() {
        itunesApi.search(inputText).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(
                call: Call<ItunesResponse?>,
                response: Response<ItunesResponse?>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()?.results

                    if (results?.isNotEmpty() == true) {
                        nothingFoundPlaceholder.visibility = View.GONE
                        somethingWrongPlaceholder.visibility = View.GONE
                        trackListAdapter.updateTrackList(results)
                    } else {
                        trackListAdapter.updateTrackList(emptyList())
                        somethingWrongPlaceholder.visibility = View.GONE
                        nothingFoundPlaceholder.visibility = View.VISIBLE
                    }
                } else {
                    trackListAdapter.updateTrackList(emptyList())
                    somethingWrongPlaceholder.visibility = View.GONE
                    somethingWrongPlaceholder.visibility = View.VISIBLE
                }
            }

            override fun onFailure(
                call: Call<ItunesResponse?>,
                t: Throwable
            ) {
                trackListAdapter.updateTrackList(emptyList())
                nothingFoundPlaceholder.visibility = View.GONE
                somethingWrongPlaceholder.visibility = View.VISIBLE
            }
        })
    }

    private fun onSearchText() {
        val searchInput = findViewById<EditText>(R.id.search_input)
        val clearInputButton = findViewById<ImageView>(R.id.clear_search_button)

        if (inputText.isNotEmpty()) {
            searchInput.setText(inputText)
        }

        searchInput.doOnTextChanged { text, _, _, _ ->
            inputText = text.toString()
            clearInputButton.visibility = if (text.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTrackList()
                true
            }
            false
        }

        clearInputButton.setOnClickListener {
            searchInput.text.clear()
            trackListAdapter.updateTrackList(emptyList())
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }
    }

    companion object {
        private const val INPUT_TEXT_KEY = "INPUT_TEXT_KEY"
        private const val DEF_INPUT_TEXT = ""
        private const val ITUNES_URL = "https://itunes.apple.com"
    }
}
