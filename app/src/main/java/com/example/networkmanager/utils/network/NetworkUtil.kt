package com.example.networkmanager.utils.network

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.networkmanager.utils.showSnackBar
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

object NetworkUtil {

    /**
     * Checks if an internet connection is there
     */
    fun Context.handleApiError(
        failure: ApiState.Failure
    ) {
        val viewGroup = (this as Activity).window?.decorView?.rootView as ViewGroup?
        when {
            failure.isNetworkError -> {
                viewGroup?.showSnackBar(message = "Please check your internet connection")
            }
            failure.errorCode == 401 -> {
                viewGroup?.showSnackBar(message = "Something went wrong..Error code 401 found..!!!")
            }
            failure.errorCode == 500 -> {
                viewGroup?.showSnackBar(message = "Something went wrong..Error code 500 found..!!!")
            }
            else -> {
                val error = failure.errorBody?.string().toString()
                if (error.isBlank() || error == "null") viewGroup?.showSnackBar(message = failure.errorMessage.toString()) else
                    viewGroup?.showSnackBar(message = error)
            }
        }
    }

    /**
     * Checks if an internet connection is there
     */
    val Context.isConnected: Boolean
        @RequiresApi(Build.VERSION_CODES.M)
        get() {
            val connectivityManager =
                this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    true
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> {
                    true
                }
                else -> {
                    false
                }
            }

        }

    /**
     * helper function  for handle [CoroutineExceptionHandler] with help of [mutableStateFlow]
     */
    fun <T> coroutineExceptionHandlerFunction(mutableStateFlow: MutableSharedFlow<ApiState<T>>): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            CoroutineScope(Dispatchers.Default).launch {
                when (exception) {
                    is HttpException -> {
                        mutableStateFlow.emit(
                            ApiState.Failure(
                                isNetworkError = false,
                                errorCode = exception.code(),
                                errorBody = exception.response()?.errorBody()
                            )
                        )
                    }
                    is UnknownHostException -> {
                        mutableStateFlow.emit(
                            ApiState.Failure(
                                isNetworkError = true,
                                errorCode = null,
                                errorBody = null
                            )
                        )
                    }
                    else -> {
                        mutableStateFlow.emit(
                            ApiState.Failure(
                                isNetworkError = false,
                                errorMessage = "Something went wrong..!!",
                                errorCode = null,
                                errorBody = null
                            )
                        )
                    }
                }
            }
        }
    }
}