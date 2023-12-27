package com.example.project_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * The main activity of the application.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button
    private lateinit var viewMapButton: Button
    private lateinit var topHeadlinesButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val SEARCH_INPUT_EXTRA = "SEARCHINPUT_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize stuff
        searchBar = findViewById(R.id.searchBar)
        searchButton = findViewById(R.id.skipButton)
        viewMapButton = findViewById(R.id.viewMapButton)
        topHeadlinesButton=findViewById(R.id.viewTopHeadlineButton )

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


        // Data persistance
        val savedSearchInput = sharedPreferences.getString(SEARCH_INPUT_EXTRA, "")
        searchBar.setText(savedSearchInput)

        //Disablebtn
        searchButton.isEnabled = false

        searchBar.addTextChangedListener(textWatcher)

        //btn func
        searchButton.setOnClickListener {
            val searchInput = searchBar.text.toString()
            val editor = sharedPreferences.edit()
            editor.putString(SEARCH_INPUT_EXTRA, searchInput)
            editor.apply()
            val intent = Intent(this@MainActivity, SourcesScreen::class.java)
            intent.putExtra(SEARCH_INPUT_EXTRA, searchInput)
            startActivity(intent)
        }

        //btn func
        viewMapButton.setOnClickListener {

            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            intent.putExtra("location", "Richmond")
            startActivity(intent)
        }

        //btn func
        topHeadlinesButton.setOnClickListener {
            val intent = Intent(this@MainActivity, TopHeadlinesScreen::class.java)
            intent.putExtra("location", "Richmond")
            startActivity(intent)
        }

    }

    /**
     * A TextWatcher that enables or disables the searchButton based on text input changes.
     */

    //searchbar inpt
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val searchInput = searchBar.text.toString()
            searchButton.isEnabled = searchInput.isNotEmpty()
        }
    }
}
