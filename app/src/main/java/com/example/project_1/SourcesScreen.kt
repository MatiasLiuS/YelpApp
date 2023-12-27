package com.example.project_1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SourcesScreen : AppCompatActivity() {
    private val newsCategoriesSet: MutableSet<String> = HashSet()
    private lateinit var recyclerView: RecyclerView
    private lateinit var skipButton: Button
    private lateinit var newsSourceAdapter: NewsSourceAdapter


    private var allNewsSources: List<NewsCategoryBusiness> = emptyList()

    companion object {
        private const val SEARCH_INPUT_EXTRA = "SEARCHINPUT_EXTRA"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sources_screen)


        /**
         *SEARCH BAR STUUFF
         * */
        val searchInput = intent.getStringExtra("SEARCHINPUT_EXTRA")

        var term = searchInput.toString()
         term = term.replace(" ","-")

        val searchInputTextView = findViewById<TextView>(R.id.headerText)

        searchInputTextView.text = "Search for: $searchInput"

        newsSourceAdapter = NewsSourceAdapter(emptyList(), term)


        val categoriesSpinner = findViewById<Spinner>(R.id.categoriesSpinner)

        val newsCategoryAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)

        newsCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoriesSpinner.adapter = newsCategoryAdapter

        val apiKey = getString(R.string.news_api_key)
        CoroutineScope(Dispatchers.IO).launch {
            val newsCategories = NewsCategoryManager().retrieveCategories(apiKey)

            withContext(Dispatchers.Main) {
                Log.d("SourcesScreen", "Received API response: $newsCategories")


                newsCategoriesSet.clear()


                val uniqueCategories = newsCategories.map { newsCategory ->
                    val category = newsCategory.category
                    capitalizeFirstLetter(category)
                }.distinct()

                newsCategoryAdapter.clear()
                newsCategoryAdapter.addAll(uniqueCategories)

                Log.d("SourcesScreen", "Received ${uniqueCategories.size} unique news categories")
                newsCategoryAdapter.notifyDataSetChanged()
            }
        }


        recyclerView = findViewById(R.id.newsRecycler)
        CoroutineScope(Dispatchers.IO).launch {
            try{
                allNewsSources = NewsCategoryManager().retrieveCategories(apiKey)

                withContext(Dispatchers.Main) {
                    val adapter = NewsSourceAdapter(allNewsSources,term)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@SourcesScreen)

                    Log.d("SourcesScreen", "Number of items retrieved from API: ${allNewsSources.size}")

                    if (allNewsSources.isNotEmpty()) {
                        Log.d("SourcesScreen", "First item: ${allNewsSources[0]}")
                    }

                }
            } catch (e: Exception) {
                Log.e("SourcesScreen", "Error fetching data: ${e.message}", e)
            }

            skipButton = findViewById(R.id.skipButton)

            skipButton.setOnClickListener {

                val intent = Intent(this@SourcesScreen, ResultsScreen::class.java)

                intent.putExtra(SEARCH_INPUT_EXTRA, searchInput)

                startActivity(intent)
            }
        }

        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedCategory = parent?.getItemAtPosition(position).toString()
                Log.d("SourcesScreen", "Selected Category: $selectedCategory")
                val filteredSources = if (selectedCategory == "general") {
                    Log.d("SourcesScreen", "we showin general")
                    allNewsSources
                } else {
                    allNewsSources.filter { it.category == selectedCategory }
                }

                Log.d("SourcesScreen", "Filtered Sources: $filteredSources")

                Log.d("NewsSourceAdapter", "Data source (After): $filteredSources")

                newsSourceAdapter.updateData(filteredSources)
                recyclerView.adapter = newsSourceAdapter

                Log.d("SourcesScreen", "Selected Category: $selectedCategory")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun capitalizeFirstLetter(input: String): String {
        return input.substring(0, 1).lowercase() + input.substring(1)
    }
}
