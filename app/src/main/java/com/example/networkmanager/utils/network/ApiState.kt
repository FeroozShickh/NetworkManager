package com.example.networkmanager.utils.network

import okhttp3.ResponseBody

sealed class ApiState<out T> {

    /**
     * Failure and network error response
     */
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int? = null,
        val errorMessage: String? = null,
        val errorBody: ResponseBody? = null
    ) : ApiState<Nothing>()

    /**
     * success response with body
     */
    data class Success<T>(val data: T) : ApiState<T>()

}