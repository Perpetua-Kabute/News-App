package com.androiddevs.mvvmnewsapp.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import java.lang.IllegalArgumentException

class NewsViewModelProviderFactory(val newsRepository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewsViewModel(newsRepository)::class.java)){
            return NewsViewModel(newsRepository) as T
        }
       throw IllegalArgumentException("Unknown viewmodel")
    }
}