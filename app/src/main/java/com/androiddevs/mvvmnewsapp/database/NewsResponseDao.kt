package com.androiddevs.mvvmnewsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

import com.androiddevs.mvvmnewsapp.models.NewsResponse

@Dao
interface NewsResponseDao {
    //save the news response I get from the api
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsResponse(newsResponse: NewsResponse): Long

    //get the news from the database
    @Query("SELECT * FROM news_responses")
    fun getSavedNewsResponse(): LiveData<NewsResponse>

    //delete current NewsResponse and add the new NewsResponse into the database
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNewsResponse(newsResponse: NewsResponse): Int

    @Query("SELECT COUNT(id) FROM news_responses")
    fun getCount(): Int



}