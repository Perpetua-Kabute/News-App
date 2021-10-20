package com.androiddevs.mvvmnewsapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse

@Database(
    entities = [Article::class, NewsResponse::class],
    version = 1
)
@TypeConverters(Convertors::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
    abstract fun getNewsResponseDao(): NewsResponseDao

    companion object{
        //Volatile means that other threads can immediately see when a thread changes an instance
        @Volatile
        private var instance: ArticleDatabase? = null

        //make sure at any one time thre is only one instance of the database
        private val LOCK = Any()

        /** called whenever an instance of a database is created this function is called
         * set to instace and if instance is null, start a synchronized block
         * return instance and if it is still null, createDatabase
         */
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{
                instance = it
        }
        }

        //create the database
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()

    }

}