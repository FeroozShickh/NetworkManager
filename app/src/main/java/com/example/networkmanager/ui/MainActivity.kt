package com.example.networkmanager.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.networkmanager.R
import com.example.networkmanager.utils.FileUtil.contentSchemeNameAndSize
import com.example.networkmanager.utils.FileUtil.copyStreamToFile
import com.example.networkmanager.utils.FileUtil.getDirectory
import com.example.networkmanager.utils.FileUtil.uploadFile
import com.example.networkmanager.network.NetworkManager
import com.example.networkmanager.utils.network.ApiState
import com.example.networkmanager.utils.network.NetworkUtil.handleApiError
import com.example.networkmanager.utils.network.NetworkUtil.isConnected
import com.example.networkmanager.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModelUser: MainViewModel by viewModels()
    private val fileUrl = "https://maven.apache.org/archives/maven-1.x/maven.pdf"

    @Inject
    lateinit var  networkManager: NetworkManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        collectFlow()
        clickEvents()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun clickEvents() {
        btnGet.setOnClickListener {
            if (!isConnected) {
                handleApiError(ApiState.Failure(isNetworkError = true))
                return@setOnClickListener
            }
            progressBar.show(isVisible = true)
            viewModelUser.callUserGetAPI()
        }

        btnPOST.setOnClickListener {
            if (!isConnected) {
                handleApiError(ApiState.Failure(isNetworkError = true))
                return@setOnClickListener
            }
            progressBar.show(isVisible = true)
            viewModelUser.callUserPOSTAPI()
        }

        btnPUT.setOnClickListener {
            if (!isConnected) {
                handleApiError(ApiState.Failure(isNetworkError = true))
                return@setOnClickListener
            }
            progressBar.show(isVisible = true)
            viewModelUser.callUserPUTAPI()
        }

        btnDelete.setOnClickListener {
            if (!isConnected) {
                handleApiError(ApiState.Failure(isNetworkError = true))
                return@setOnClickListener
            }
            progressBar.show(isVisible = true)
            viewModelUser.callUserDELETEAPI()
        }

        btnDownload.setOnClickListener {
            if (!isConnected) {
                handleApiError(ApiState.Failure(isNetworkError = true))
                return@setOnClickListener
            }
            progressBar.show(isVisible = true)
            networkManager.downloadFile(context = applicationContext, fileUrl = fileUrl, { percentage ->
                    txtPercentage.text = percentage.toString()
                }, { errorMessage ->
                    progressBar.show(isVisible = false)
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }, { successMessage ->
                    progressBar.show(isVisible = false)
                    Toast.makeText(applicationContext, successMessage, Toast.LENGTH_SHORT)
                        .show()
                })
        }

        btnUpload.setOnClickListener {
            if (!isConnected) {
                handleApiError(ApiState.Failure(isNetworkError = true))
                return@setOnClickListener
            }
            progressBar.show(isVisible = true)
            selectDocumentFromDevice()
        }

    }

    private fun collectFlow() {
        lifecycleScope.launch {
            viewModelUser.getUserDataStateFlow.collect {
                progressBar.show(isVisible = false)
                when (it) {
                    is ApiState.Failure -> handleApiError(it)
                    is ApiState.Success<*> -> {
                        Toast.makeText(
                            applicationContext,
                            "Data fetched successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModelUser.postUserDataStateFlow.collect {
                progressBar.show(isVisible = false)
                when (it) {
                    is ApiState.Failure -> handleApiError(it)
                    is ApiState.Success<*> -> {
                        Toast.makeText(
                            applicationContext,
                            "New Data created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModelUser.putUserDataStateFlow.collect {
                progressBar.show(isVisible = false)
                when (it) {
                    is ApiState.Failure -> handleApiError(it)
                    is ApiState.Success<*> -> {
                        Toast.makeText(
                            applicationContext,
                            "Data updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModelUser.deleteUserDataStateFlow.collect {
                progressBar.show(isVisible = false)
                when (it) {
                    is ApiState.Failure -> handleApiError(it)
                    is ApiState.Success<*> -> {
                        Toast.makeText(
                            applicationContext,
                            "Data deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    /**
     * Result call back from document intent
     * */
    private var resultLauncherForDocument =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { returnUri ->
                    val nameAndSizeDetails = returnUri.contentSchemeNameAndSize(applicationContext)
                    Log.d(TAG, "nameandsize $nameAndSizeDetails")
                    val inputStream = this.contentResolver.openInputStream(returnUri)
                    val directory = getDirectory(this, "files")
                    val tempFile =
                        File(directory.path + File.separator + nameAndSizeDetails?.first)

                    inputStream?.let {
                        val outputFile = copyStreamToFile(it, tempFile)
                        progressBar.show(isVisible = false)
                        uploadFile(file = outputFile) { fileToUpload, fileName ->
                            viewModelUser.calUploadFileApi(fileToUpload, fileName)
                        }
                    } ?: kotlin.run {
                        progressBar.show(isVisible = false)
                    }
                } ?: kotlin.run {
                    progressBar.show(isVisible = false)
                }
            } else {
                progressBar.show(isVisible = false)
            }
        }


    /**
     * Open document storage from the device
     * */
    private fun selectDocumentFromDevice() { //supporting file as doc, docx and pdf
        val mimeTypes = arrayOf("application/*", "image/*", "video/*", "audio/*")
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }.run {
            resultLauncherForDocument.launch(this)
        }
    }
}