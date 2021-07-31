package com.dxn.androidstorage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.dxn.androidstorage.ui.theme.AndroidStorageTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var isPrivate: MutableState<Boolean>
    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            if(isPrivate.value) {
              viewModel.savePhotoToInternalStorage(System.currentTimeMillis().toString(),it)
            } else {

            }
        }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<MainActivityViewModel>().value

        setContent {
            AndroidStorageTheme {
                Surface(color = MaterialTheme.colors.background) {

                    val internalStoragePhoto by remember { viewModel.internalStoragePhotos }
                    val sharedStoragePhoto by remember { viewModel.sharedStoragePhotos }
                    isPrivate = remember { mutableStateOf(false) }

                    Column(Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Internal Storage Photo")
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(Modifier.fillMaxHeight(0.45f)) {
                            items(items = internalStoragePhoto) { photo ->
                                Image(
                                    bitmap = photo.bmp.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.height(180.dp).clickable {
                                        lifecycleScope.launch {
                                            viewModel.deleteInternalStoragePhoto(photo.name)
                                            viewModel.loadPhotos()
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Shared Storage Photo")
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn( Modifier.fillMaxHeight(0.45f)) {
                            items(items = sharedStoragePhoto) { photo ->

                            }
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Button(onClick = {
                                takePhoto.launch()
                                viewModel.loadPhotos()

                            }) {
                                Text(text = "Take Photo")
                            }
                            Switch(checked = isPrivate.value, onCheckedChange = {
                                isPrivate.value = !isPrivate.value
                            })
                        }
                    }
                }
            }
        }
    }
}







