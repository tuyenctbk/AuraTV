package com.example.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object MediaPickerHelper {

    suspend fun savePickedUriLocally(
        context: Context,
        uri: Uri,
        subfolder: String,
        prefix: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val dir = File(context.filesDir, subfolder).apply { mkdirs() }
            val fileName = "${prefix}_${System.currentTimeMillis()}.jpg"
            val targetFile = File(dir, fileName)

            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return@withContext null

            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            uri.toString()
        }
    }
}
