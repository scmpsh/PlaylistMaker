package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged

class SearchActivity : AppCompatActivity() {

    private var inputText: String = DEF_INPUT_TEXT

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

        backButton.setOnClickListener { finish() }

        onSearchText()
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

        clearInputButton.setOnClickListener {
            searchInput.text.clear()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }
    }

    companion object {
        const val INPUT_TEXT_KEY = "INPUT_TEXT_KEY"
        const val DEF_INPUT_TEXT = ""
    }
}
