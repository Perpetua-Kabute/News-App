package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.util.PrivateConstants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        //lazy means it'll be initialized once
        private val retrofit by lazy {
            //use HttpLoggingInterceptor to log the responses from retrofit
            val logging = HttpLoggingInterceptor()
            //log the response body
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            //use interceptor to create a network client
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        //create api object to make network requests from anywhere
        val api by lazy{
            retrofit.create(NewsApi::class.java)
        }


    }
}