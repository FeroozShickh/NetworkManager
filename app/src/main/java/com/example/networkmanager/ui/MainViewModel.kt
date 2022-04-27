package com.example.networkmanager.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networkmanager.data.model.request.NewUser
import com.example.networkmanager.data.model.request.UpdateUser
import com.example.networkmanager.data.repository.UserRepository
import com.example.networkmanager.utils.network.ApiState
import com.example.networkmanager.utils.network.NetworkUtil.coroutineExceptionHandlerFunction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * A View model for [MainActivity]
 * */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private lateinit var userId: String

    private val getUserDataMutableStateFlow: MutableSharedFlow<ApiState<Boolean>> =
        MutableSharedFlow()
    val getUserDataStateFlow = getUserDataMutableStateFlow.asSharedFlow()

    private val postUserDataMutableStateFlow: MutableSharedFlow<ApiState<Boolean>> =
        MutableSharedFlow()
    val postUserDataStateFlow = postUserDataMutableStateFlow.asSharedFlow()

    private val putUserDataMutableStateFlow: MutableSharedFlow<ApiState<Boolean>> =
        MutableSharedFlow()
    val putUserDataStateFlow = putUserDataMutableStateFlow.asSharedFlow()

    private val deleteUserDataMutableStateFlow: MutableSharedFlow<ApiState<Boolean>> =
        MutableSharedFlow()
    val deleteUserDataStateFlow = deleteUserDataMutableStateFlow.asSharedFlow()

    /**
     * Call user get API
     * */
    fun callUserGetAPI() =
        viewModelScope.launch(coroutineExceptionHandlerFunction(mutableStateFlow = getUserDataMutableStateFlow)) {
            userRepository.getUserList(pageDetail = 1).collect {
                getUserDataMutableStateFlow.emit(ApiState.Success(data = true))
            }
        }

    /**
     * Call user POST API
     * */
    fun callUserPOSTAPI() =
        viewModelScope.launch(coroutineExceptionHandlerFunction(mutableStateFlow = postUserDataMutableStateFlow)) {
            userRepository.createUser(userDetails = NewUser()).collect {
                it.let { userDetails ->
                    userId = userDetails.response?.id.toString()
                }
                postUserDataMutableStateFlow.emit(ApiState.Success(data = true))
            }
        }

    /**
     * Call user PUT API
     * */
    fun callUserPUTAPI() =
        viewModelScope.launch(coroutineExceptionHandlerFunction(mutableStateFlow = putUserDataMutableStateFlow)) {
            userRepository.updateUser(userId = userId.ifBlank { 0 }, userDetails = UpdateUser())
                .collect {
                    putUserDataMutableStateFlow.emit(ApiState.Success(data = true))
                }
        }

    /**
     * Call user DELETE API
     * */
    fun callUserDELETEAPI() =
        viewModelScope.launch(coroutineExceptionHandlerFunction(mutableStateFlow = deleteUserDataMutableStateFlow)) {
            userRepository.deleteUser(userId = userId.ifBlank { 0 }).collect {
                deleteUserDataMutableStateFlow.emit(ApiState.Success(data = true))
            }
        }

    /**
     * Call user's file upload API
     * */
    fun calUploadFileApi(file: MultipartBody.Part, filename: RequestBody) = viewModelScope.launch {
        try {
            userRepository.upload(file, filename, NewUser()).collect {
                Log.d("View Model", "File Uploaded Successful")
            }
        } catch (e: Exception) {
            Log.d("View Model", e.toString())
        }
    }
}