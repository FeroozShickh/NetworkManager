package com.example.networkmanager.data.repository

import com.example.networkmanager.data.model.AppApiResponse
import com.example.networkmanager.data.model.response.User
import com.example.networkmanager.data.model.response.UserCreateResponse
import com.example.networkmanager.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * Repository to fetch data from remote with help of [ApiService]
 * */
class UserRepository @Inject constructor(
    private val apiService: ApiService,
) {

    /**
     * Function to call user list api
     */
    fun getUserList(pageDetail: Any): Flow<User> {
        return flow {
            val result = apiService.getUserList(page = pageDetail)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Function to call create user api
     */

    fun createUser(
        userDetails: Any
    ): Flow<AppApiResponse<UserCreateResponse>> {
        return flow {
            val result = apiService.createUser(userDetails = userDetails)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Function to call update user api
     */
    fun updateUser(userId: Any, userDetails: Any): Flow<AppApiResponse<UserCreateResponse>> {
        return flow {
            val result = apiService.updateUser(id = userId, userDetails = userDetails)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Function to call delete user api
     */
    fun deleteUser(userId: Any): Flow<AppApiResponse<UserCreateResponse>> {
        return flow {
            val result = apiService.deleteUser(id = userId)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Function to call upload user's file api
     */
    fun upload(
        file: MultipartBody.Part,
        filename: RequestBody,
        user: Any
    ): Flow<AppApiResponse<UserCreateResponse>> {
        return flow {
            val result = apiService.uploadFile(file, filename, user)
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}