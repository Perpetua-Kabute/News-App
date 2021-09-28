package com.androiddevs.mvvmnewsapp.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    val newsRepository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) =
        viewModelScope.launch {
            breakingNews.postValue(Resource.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
            breakingNews.postValue(handleBreakingNewsResponse(response))
        }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun searchForNews(searchQuery: String) =
        viewModelScope.launch {
            searchNews.postValue(Resource.Loading())
            val searchResponse = newsRepository.searchForNews(searchQuery , searchNewsPage)
            searchNews.postValue(handleSearchNewsResponse(searchResponse))

        }

    private fun handleSearchNewsResponse(searchResponse: Response<NewsResponse>): Resource<NewsResponse>? {
        if(searchResponse.isSuccessful){
            searchResponse.body()?.let{
                return Resource.Success(it)
            }
        }
        return Resource.Error(searchResponse.message())
    }
}