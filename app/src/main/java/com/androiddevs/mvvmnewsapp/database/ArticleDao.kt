package com.androiddevs.mvvmnewsapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevs.mvvmnewsapp.models.Article

//annotate so room knows that it is the interface that defines functions
@Dao
interface ArticleDao {

    //insert or update an article
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    //return all available articles
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    //delete article
    @Delete
    suspend fun deleteArticle(article: Article)
}