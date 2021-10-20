package com.androiddevs.mvvmnewsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "news_responses"
)
data class NewsResponse(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)