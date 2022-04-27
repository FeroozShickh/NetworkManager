package com.example.networkmanager.data.model.response


import com.google.gson.annotations.SerializedName

data class UserCreateResponse(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("job")
    val job: String,
    @SerializedName("name")
    val name: String
)