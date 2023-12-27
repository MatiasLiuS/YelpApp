package com.example.project_1

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson

data class ApiTHResponse(
    val status: String,
    val articles: List<THBusiness>?
)

class THManager {
    private val gson = Gson()
    private val okHttpClient: OkHttpClient = OkHttpClient()

    fun retrieveTH(apiKey: String, category: String,page: Int ): List<THBusiness> {
        val url =
            "https://newsapi.org/v2/top-headlines?country=us&category=$category&page=$page&pageSize=8&apiKey=$apiKey"
        Log.d("THManager", "API KEY $apiKey")
        Log.d("THManager", "CAT KEY $category")
        Log.d("THManager", "URL KEY $url")
        val request = Request.Builder()
            .url(url)
            .build()
        Log.d("THManager", "Before making API request")
        val response = okHttpClient.newCall(request).execute()
        Log.d("THManager", "After making API request")

        val responseBody: String? = response.body?.string()
        Log.d("THManager", "API Response Body: $responseBody")

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            Log.d("THManager", "Passes if statement")
            val apiResponse = gson.fromJson(responseBody, ApiTHResponse::class.java)
            Log.d("THManager", "2nd API Response Body: $apiResponse")
            return apiResponse.articles ?: emptyList()
        }
            return emptyList()

    }
}