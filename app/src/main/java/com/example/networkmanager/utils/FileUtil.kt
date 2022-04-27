package com.example.networkmanager.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtil {

    /**
     * Function to create multipart body for upload a file
     */
    fun uploadFile(
        file: File,
        fileToUploadDetails: ((MultipartBody.Part, RequestBody) -> Unit)
    ) {
        val requestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
        val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)

        val filename: RequestBody = file.asRequestBody("text/plain".toMediaTypeOrNull())
        fileToUploadDetails.invoke(fileToUpload, filename)
    }

    /**
     * Function to copy the stream to the file
     */
    fun copyStreamToFile(inputStream: InputStream, outputFile: File): File {
        inputStream.use { input ->
            val outputStream = FileOutputStream(outputFile)
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = input.read(buffer)
                    if (byteCount < 0) break
                    output.write(buffer, 0, byteCount)
                }
                output.flush()
            }
        }
        return outputFile
    }

    /**
     * Function to create the directory for storing the file
     */
    fun getDirectory(context: Context, dirName: String): File {
        val file = File(context.filesDir.path + File.separator + dirName)
        if (!file.exists()) file.mkdir()
        return file
    }

    /**
     * Function to return file name and size with help of [context]
     */
    fun Uri.contentSchemeNameAndSize(context: Context): Pair<String, Long>? {
        return context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            val name = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val size = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.getString(name) to cursor.getLong(size)
        }
    }

}