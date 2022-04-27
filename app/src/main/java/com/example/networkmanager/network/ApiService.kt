package com.example.networkmanager.network

import com.example.networkmanager.data.model.AppApiResponse
import com.example.networkmanager.data.model.response.User
import com.example.networkmanager.data.model.response.UserCreateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @GET(EndPoint.API_GET_USER)
    suspend fun getUserList(
        @Query("page") page: Any
    ): User


    @POST(EndPoint.API_GET_USER)
    suspend fun createUser(
        @Body userDetails: Any
    ): AppApiResponse<UserCreateResponse>


    @PUT(EndPoint.API_GET_USER.plus("/{id}"))
    suspend fun updateUser(
        @Path("id") id: Any,
        @Body userDetails: Any
    ): AppApiResponse<UserCreateResponse>


    @DELETE(EndPoint.API_GET_USER.plus("/{id}"))
    suspend fun deleteUser(
        @Path("id") id: Any
    ): AppApiResponse<UserCreateResponse>

    @Multipart
    @POST(EndPoint.UPLOAD_FILE)
    fun uploadFile(@Part file: MultipartBody.Part,
                   @Part("file") name: RequestBody,
                   @Part( "user") user:Any
    ): AppApiResponse<UserCreateResponse>

}