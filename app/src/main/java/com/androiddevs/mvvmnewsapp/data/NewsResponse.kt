package com.androiddevs.mvvmnewsapp.data

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)