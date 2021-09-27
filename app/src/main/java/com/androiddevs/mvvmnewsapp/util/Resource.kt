package com.androiddevs.mvvmnewsapp.util

/**
 * A class to be used to wrap around network responses
 * helps differentiate between successful and error messages and handle loading state
 *
 * Sealed class means it defines classes that inherit it
 */
sealed class Resource<T> (
    val data : T? = null, //body of response
    val message: String? = null //eg error message
    ){
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}