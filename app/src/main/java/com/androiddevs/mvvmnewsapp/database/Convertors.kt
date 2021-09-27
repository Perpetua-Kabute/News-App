package com.androiddevs.mvvmnewsapp.database

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.data.Source

class Convertors {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}