package com.androiddevs.mvvmnewsapp.ui.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import java.lang.IllegalArgumentException

class NewsViewModelProviderFactory(
    val app: Application,
    val newsRepository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewsViewModel(app, newsRepository)::class.java)){
            return NewsViewModel(app, newsRepository) as T
        }
       throw IllegalArgumentException("Unknown viewmodel")
    }
}