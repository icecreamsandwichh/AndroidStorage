package com.dxn.androidstorage

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dxn.androidstorage.data.InternalStoragePhoto
import com.dxn.androidstorage.data.SharedStoragePhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivityViewModel(private val app: Application) : AndroidViewModel(app) {

    val internalStoragePhotos = mutableStateOf<List<InternalStoragePhoto>>(listOf())
    val sharedStoragePhotos = mutableStateOf<List<SharedStoragePhoto>>(listOf())

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            internalStoragePhotos.value = getInternalStoragePhotos()
        }
    }

    fun savePhotoToInternalStorage(filename: String, bitmap: Bitmap): Boolean {
        return try {
            app.openFileOutput("$filename.jpg", ComponentActivity.MODE_PRIVATE).use { stream ->
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    throw IOException("Cannot save bitmap to JPEG")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun getInternalStoragePhotos(): List<InternalStoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = app.filesDir.listFiles()
            files?.filter {
                it.canRead() && it.isFile && it.name.endsWith(".jpg")
            }?.map {
                val bytes = it.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStoragePhoto(it.name, bitmap)
            } ?: listOf()
        }
    }


    suspend fun deleteInternalStoragePhoto(filename: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                app.deleteFile(filename)
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }
}