package com.example.project_1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.content.SharedPreferences
import android.widget.Button
import android.widget.TextView

class TopHeadlinesScreen: AppCompatActivity() {
    private val newsCategoriesSet: MutableSet<String> = HashSet()
    private lateinit var recyclerView: RecyclerView
    private var allNewsSources: List<THBusiness> = emptyList()
    private lateinit var THAdapter: THAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var currentPage = 1
    private var selectedCategory = "general"
    val maxPage = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_headlines_screen)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)



        val pageInfoText = findViewById<TextView>(R.id.pageInfoText)

        pageInfoText.text = "Page $currentPage of $maxPage"
        /**
         *SPINNER BAR STUUFF
         * */

        var category = ""
        val categoriesSpinner = findViewById<Spinner>(R.id.categoriesSpinner)

        val newsCategoryAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)

        val savedCategory = sharedPreferences.getString("selectedCategory", "general")
        Log.d("TopHeadlinesScreen", "Saved category: $savedCategory")

        category = savedCategory ?: "general"


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


        THAdapter = THAdapter(emptyList())
        recyclerView = findViewById(R.id.newsRecycler)


        CoroutineScope(Dispatchers.IO).launch {
            try {
                allNewsSources = THManager().retrieveTH(apiKey, category, currentPage)

                withContext(Dispatchers.Main) {
                    val adapter = THAdapter(allNewsSources)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@TopHeadlinesScreen)

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
        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedCategory = parent?.getItemAtPosition(position).toString()
                Log.d("TopHeadlinesScreen", "Selected category: $selectedCategory")
                sharedPreferences.edit().putString("selectedCategory", selectedCategory).apply()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val newNewsSources = THManager().retrieveTH(apiKey, selectedCategory, currentPage)

                        withContext(Dispatchers.Main) {
                            allNewsSources = newNewsSources
                            val adapter =
                                THAdapter(allNewsSources)
                            recyclerView.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        Log.e("SourcesScreen", "Error fetching data: ${e.message}", e)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        val previousButton = findViewById<Button>(R.id.previousButton)
        val nextButton = findViewById<Button>(R.id.nextButton)
        updateButtonStates()
        previousButton.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                fetchNewsData(apiKey)
                updateButtonStates()
                updatePageInfo(pageInfoText)
            }
        }

        nextButton.setOnClickListener {
            currentPage++
            fetchNewsData(apiKey)
            updateButtonStates()
            updatePageInfo(pageInfoText)
        }
    }

    private fun capitalizeFirstLetter(input: String): String {
        return input.substring(0, 1).lowercase() + input.substring(1)
    }


    private fun updateButtonStates() {
        val previousButton = findViewById<Button>(R.id.previousButton)
        val nextButton = findViewById<Button>(R.id.nextButton)
        nextButton.isEnabled = currentPage <= maxPage-1
        previousButton.isEnabled = currentPage > 1
    }

    private fun fetchNewsData(apiKey: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val newNewsSources = THManager().retrieveTH(apiKey, selectedCategory, currentPage, )

                withContext(Dispatchers.Main) {
                    allNewsSources = newNewsSources
                    val adapter = THAdapter(allNewsSources)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("SourcesScreen", "Error fetching data: ${e.message}", e)
            }
        }
    }
    private fun updatePageInfo(pageInfoText: TextView) {
        pageInfoText.text = "Page $currentPage of $maxPage"
    }
}
