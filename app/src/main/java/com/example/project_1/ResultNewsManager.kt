package com.example.project_1

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson


data class ApiResultsResponse(
    val status: String,
    val articles: List<MapNewsBusiness>?
)

class ResultNewsManager {
    private val gson = Gson()
    private val okHttpClient: OkHttpClient = OkHttpClient()

    fun retrieveResultNews(apiKey: String, term: String, ): List<MapNewsBusiness> {
        Log.d("ResultNewsManager", "TermBefore $term")
        val modifiedTerm = term.replace(" ", "-")
        Log.d("ResultNewsManager", "TermAfter $modifiedTerm")
        val url = "https://newsapi.org/v2/everything?q=$modifiedTerm&apiKey=$apiKey"
        Log.d("ResultNewsManager", "API KEY $apiKey")
        Log.d("ResultNewsManager", "TERM KEY $modifiedTerm")
        Log.d("ResultNewsManager", "Skip URL KEY $url")
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("ResultNewsManager", "Before making API request")
        val response = okHttpClient.newCall(request).execute()
        Log.d("ResultNewsManager", "After making API request")

        val responseBody: String? = response.body?.string()
        Log.d("ResultNewsManager", "API Response Body: $responseBody")

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            Log.d("ResultNewsManager", "Passes if statement")
            val apiResponse = gson.fromJson(responseBody, ApiResultsResponse::class.java)
            Log.d("ResultNewsManager", "2nd API Response Body: $apiResponse")
            return apiResponse.articles ?: emptyList()
        } else {
            return emptyList()
        }
    }

    fun retrieveSourceResultNews(apiKey: String, term: String, id: String): List<MapNewsBusiness> {
        val url = "https://newsapi.org/v2/everything?q=$term&sources=$id&apiKey=$apiKey"
        Log.d("ResultNewsManager", "API KEY $apiKey")
        Log.d("ResultNewsManager", "ID KEY $id")
        Log.d("ResultNewsManager", "TERM KEY $term")
        Log.d("ResultNewsManager", " source URL KEY $url")
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("ResultNewsManager", "Before making API request")
        val response = okHttpClient.newCall(request).execute()
        Log.d("ResultNewsManager", "After making API request")

        val responseBody: String? = response.body?.string()
        Log.d("ResultNewsManager", "API Response Body: $responseBody")

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            Log.d("ResultNewsManager", "Passes if statement")
            val apiResponse = gson.fromJson(responseBody, ApiResultsResponse::class.java)
            Log.d("ResultNewsManager", "2nd API Response Body: $apiResponse")
            return apiResponse.articles ?: emptyList()
        } else {
            return emptyList()
        }
    }
}

