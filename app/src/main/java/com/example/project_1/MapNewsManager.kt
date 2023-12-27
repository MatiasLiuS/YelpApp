package com.example.project_1

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson

data class ApiMapResponse(
    val status: String,
    val articles: List<MapNewsBusiness>?
)

class MapNewsManager {
    private val gson = Gson()
    private val okHttpClient: OkHttpClient = OkHttpClient()

    fun retrieveMapNews(apiKey: String, state: String): List<MapNewsBusiness> {
       val url = "https://newsapi.org/v2/everything?q=$state&apiKey=$apiKey"
        Log.d("MapNewsManager", "API KEY $apiKey")
        Log.d("MapNewsManager", "STATE KEY $state")
        Log.d("MapNewsManager", "URL KEY $url")
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("MapNewsManager", "Before making API request")
        val response = okHttpClient.newCall(request).execute()
        Log.d("MapNewsManager", "After making API request")

        val responseBody: String? = response.body?.string()
        Log.d("MapsActivity", "API Response Body: $responseBody")

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            Log.d("MapsActivity", "Passes if statement")
            val apiResponse = gson.fromJson(responseBody, ApiMapResponse::class.java)
            Log.d("MapsActivity", "2nd API Response Body: $apiResponse")
            return apiResponse.articles ?: emptyList()
        } else {
            return emptyList()
        }
    }

}
