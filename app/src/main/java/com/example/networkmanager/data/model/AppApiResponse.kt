package com.example.networkmanager.data.model

import com.google.gson.annotations.SerializedName

/**
 * A generic class to map all the API response
 * @param T the generic type which varies with the API
 * @property status indicates the status of the API response
 * @property error indicates error
 * @property errorPriority priority of the error
 * @property statusCode statusCode
 * @property response the generic type of response
 */
@Suppress("HardCodedStringLiteral")
data class AppApiResponse<T>(
    @SerializedName("Status")
    var status: Boolean?,
    @SerializedName("Error")
    var error: String?,
    @SerializedName("ErrorPriority")
    var errorPriority: String?,
    @SerializedName("StatusCode")
    var statusCode: Int?,
    @SerializedName("response")
    var response: T?,
)