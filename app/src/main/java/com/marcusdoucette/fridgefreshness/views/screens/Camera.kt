package com.marcusdoucette.fridgefreshness.views.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marcusdoucette.fridgefreshness.FFAppView
import com.marcusdoucette.fridgefreshness.views.models.NewItemViewModel
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraView(switchView: (FFAppView,Int?)->Unit,
               modifier:Modifier = Modifier,
               vm: NewItemViewModel = viewModel()){
    val imageCapture = remember{ ImageCapture.Builder().build()}
    val context = LocalContext.current

    Box(modifier.fillMaxSize()){
        CameraPreviewScreen(
            context,
            imageCapture,
            onCapture = {
                captureImage(imageCapture,context,vm::setBM)
                switchView(FFAppView.NEW_ITEM,null)
            },
            onCancel = {
                switchView(FFAppView.NEW_ITEM,null)
            }
        )
    }
}



@Composable
fun CameraPreviewScreen(context:Context,imageCapture:ImageCapture,onCapture:()->Unit,onCancel:()->Unit){
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val previewView = remember{
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    LaunchedEffect(lensFacing){
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner,cameraxSelector, preview,imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    Box(contentAlignment = Alignment.BottomCenter,modifier = Modifier.fillMaxSize()){
        AndroidView(factory = {previewView}, modifier = Modifier.fillMaxSize())
        Row(horizontalArrangement = Arrangement.SpaceAround){
            Button(onClick = onCancel){
                Text(text = "Cancel")
            }
            Button(onClick = onCapture){
                Text(text = "Capture Image")
            }
        }

    }

}

//fun captureImageOld(imageCapture: ImageCapture, context: Context) {
//    val name = "CameraxImage.jpeg"
//    val contentValues = ContentValues().apply{
//        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//        }
//    }
//    val outputOptions = ImageCapture.OutputFileOptions
//        .Builder(
//            context.contentResolver,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            contentValues
//        )
//        .build()
//    imageCapture.takePicture(
//        outputOptions,
//        ContextCompat.getMainExecutor(context),
//        object: ImageCapture.OnImageSavedCallback{
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults){
//                println("Success")
//            }
//
//            override fun onError(exception: ImageCaptureException){
//                println("Failed $exception")
//            }
//        }
//    )
//
//}

fun captureImage(imageCapture:ImageCapture,context:Context,callback:(Bitmap)->Unit){
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object: ImageCapture.OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy){
                val bitmap=imageProxyToBitmap(image)
                callback(bitmap)
                super.onCaptureSuccess(image)
            }
            override fun onError(exception:ImageCaptureException){
                super.onError(exception)
            }
        }
    )
}

fun imageProxyToBitmap(image:ImageProxy): Bitmap {
    val planeProxy = image.planes[0]
    val buffer: ByteBuffer =planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation->
        ProcessCameraProvider.getInstance(this).also{ cameraProvider->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }