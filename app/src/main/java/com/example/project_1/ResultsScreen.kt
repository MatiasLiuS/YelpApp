package com.example.project_1

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultsScreen : AppCompatActivity() {
    private var allNewsSources: List<MapNewsBusiness> = emptyList()
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_screen)

        val searchInput = intent.getStringExtra("SEARCHINPUT_EXTRA")
        val term = searchInput.toString()
        Log.d("ResultsScreen",
            "term: $term")
        val sourceName = intent.getStringExtra("name")
        val searchInputTextView = findViewById<TextView>(R.id.headerText)
        val source = intent.getStringExtra("id")



        val apiKey = getString(R.string.news_api_key)
        recyclerView = findViewById(R.id.newsRecycler)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (source != null) {
                    allNewsSources = ResultNewsManager().retrieveSourceResultNews(apiKey, term, source)
                }else{
                allNewsSources = ResultNewsManager().retrieveResultNews(apiKey, term)
                }
                withContext(Dispatchers.Main) {
                    val adapter = MapNewsAdapter(allNewsSources)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@ResultsScreen)

                    Log.d(
                        "SourcesScreen",
                        "Number of items retrieved from API: ${allNewsSources.size}"
                    )

                    if (allNewsSources.isNotEmpty()) {
                        Log.d("SourcesScreen", "First item: ${allNewsSources[0]}")
                    }

                }
            } catch (e: Exception) {
                Log.e("SourcesScreen", "Error fetching data: ${e.message}", e)
            }
        }

        var text = searchInput.toString()
        text = text.replace("-"," ")
        if (source != null) {
            searchInputTextView.text = "$sourceName Results for: $text"
        }else {
            searchInputTextView.text = "Results for: $text"
        }
    }
}