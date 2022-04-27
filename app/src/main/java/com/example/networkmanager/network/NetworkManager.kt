package com.example.networkmanager.network

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.networkmanager.utils.Util
import com.example.networkmanager.utils.network.NetworkUtil.isConnected
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.*
import java.net.URL
import java.net.URLConnection

/**
 * Helper class for download a file
 * */
class NetworkManager {

//    companion object {
//        fun getInstance(): NetworkManager = NetworkManager()
//    }

    /**
     * Download file from url by help of [context], [fileUrl], [downloadPercentage], [onDownloadError] and [onDownloadSuccess]
     * */
    @RequiresApi(Build.VERSION_CODES.M)
    fun downloadFile(
        context: Context,
        fileUrl: String,
        downloadPercentage: ((Int) -> Unit),
        onDownloadError: ((String) -> Unit),
        onDownloadSuccess: ((String) -> Unit),
    ) {
        if (!context.isConnected) {
            onDownloadError.invoke("Check your internet connection...!!")
            return
        }

        CoroutineScope(IO).launch {
            kotlin.runCatching {
                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null
                try {
                    var count: Int
                    val url = URL(fileUrl)
                    val urlConnection: URLConnection = url.openConnection()
                    urlConnection.connect()

                    val lengthOfFile: Int = urlConnection.contentLength
                    inputStream = BufferedInputStream(url.openStream(), 8192)

                    // Output stream to write file
                    val storageDir = File(
                        getApplication(context).applicationContext.getExternalFilesDir(null)
                            .toString()
                    )
                    storageDir.mkdirs()
                    val file = File(storageDir, Util.getTheLastPartOfTheUrl(relativeUrl = fileUrl))
                    outputStream = FileOutputStream(file)

                    val data = ByteArray(1024)
                    var total: Long = 0
                    while (inputStream.read(data).also { count = it } != -1) {
                        total += count
                        withContext(Main) {
                            downloadPercentage.invoke((total * 100 / lengthOfFile).toInt())
                        }
                        outputStream.write(data, 0, count)
                    }
                    outputStream.flush()
                    withContext(Main) {
                        onDownloadSuccess.invoke("File Downloaded Successfully.")
                    }
                } catch (e: java.lang.Exception) {
                    withContext(Main) {
                        onDownloadError.invoke("Something went wrong...!!")
                    }
                } finally {
                    outputStream?.close()
                    inputStream?.close()
                }
            }
        }
    }
}