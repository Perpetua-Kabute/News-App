package com.androiddevs.mvvmnewsapp.database

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Convertors {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }

    @TypeConverter
    fun fromStringToArticle(data: String): List<Article>{
        val gson = Gson()
        if(data == null){
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<Article>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun ArticleToString(articles: List<Article>): String{
        val gson = Gson()
        return gson.toJson(articles)
    }
}