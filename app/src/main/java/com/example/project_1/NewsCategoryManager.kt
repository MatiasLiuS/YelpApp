package com.example.project_1

import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson


data class ApiResponse(
    val status: String,
    val sources: List<NewsCategoryBusiness>?
)


class NewsCategoryManager {
    private val gson = Gson()
    private val okHttpClient: OkHttpClient = OkHttpClient()


    fun retrieveCategories(apiKey: String): List<NewsCategoryBusiness> {
        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines/sources?country=us&language=en&apiKey=$apiKey")
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            val apiResponse = gson.fromJson(responseBody, ApiResponse::class.java)
            return apiResponse.sources ?: emptyList()
        } else {
            return emptyList()
        }
    }
}
