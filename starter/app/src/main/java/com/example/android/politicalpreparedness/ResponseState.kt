package com.example.android.politicalpreparedness

sealed class ResponseState<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResponseState<T>()
    data class Error(val message: String?) : ResponseState<Nothing>()
}
