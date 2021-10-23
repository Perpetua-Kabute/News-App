package com.androiddevs.mvvmnewsapp.ui.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.NewsApplication
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    val app: Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) =
        viewModelScope.launch(Dispatchers.IO) {
            safeBreakingNewsCall(countryCode)
        }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                handleBreakingNewsResponse(response)
            }
        } catch (t: Throwable){
            when(t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    fun saveNewsResponse(newsResponse: NewsResponse) =
        viewModelScope.launch {
            newsRepository.insertNewsResponse(newsResponse)
        }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Boolean{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse ->
                breakingNewsPage++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                } else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                updateNewsResponse(breakingNewsResponse ?: resultResponse)
                
                return true
            }
        }

        return false
    }


    fun searchForNews(searchQuery: String) =
        viewModelScope.launch {
            safeSearchNewsCall(searchQuery)
        }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val searchResponse = newsRepository.searchForNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(searchResponse))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable){
            when(t) {
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    /**
     * Handle pagination by saving the current response in the viewModel
     * check if the response saved is null or not
     */
    private fun handleSearchNewsResponse(searchResponse: Response<NewsResponse>): Resource<NewsResponse>? {
        if(searchResponse.isSuccessful){
            searchResponse.body()?.let{ resultResponse ->
                searchNewsPage++
                if(searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                } else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }

                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(searchResponse.message())
    }

    fun getSavedNewsResponse() =
        newsRepository.getCurrentNewsResponse()

    fun updateNewsResponse(response: NewsResponse) =
        viewModelScope.launch {
            newsRepository.updateNewsResponse(response)
        }



    fun saveArticle(article: Article) =
        viewModelScope.launch {
            newsRepository.upsert(article)
        }

    fun getSavedNews() =
        newsRepository.getSavedNews()

    fun deleteArticle(article: Article) =
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }



    private fun hasInternetConnection() : Boolean{
        //use connectivity manager using context
        //inside AndroidViewModel we can use Application Context that lives as long as the application does

        //detect if user is currently connected to the internet or not
        val connectivitymanager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        //version is bigger than api 23
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivitymanager.activeNetwork ?: return false
            val capabilities =  connectivitymanager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else {
            connectivitymanager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}