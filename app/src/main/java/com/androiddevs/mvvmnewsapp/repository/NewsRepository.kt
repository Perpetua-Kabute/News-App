package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.database.ArticleDatabase
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) =
        db.getArticleDao().upsert(article)

    fun getSavedNews() =
        db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) =
        db.getArticleDao().deleteArticle(article)

    suspend fun insertNewsResponse(newsResponse: NewsResponse)=
        db.getNewsResponseDao().insertNewsResponse(newsResponse)

    fun getCurrentNewsResponse() =
        db.getNewsResponseDao().getSavedNewsResponse()

    suspend fun updateNewsResponse(newsResponse: NewsResponse){
        db.getNewsResponseDao().updateNewsResponse(newsResponse)
    }

    fun getcount() = db.getNewsResponseDao().getCount()
}