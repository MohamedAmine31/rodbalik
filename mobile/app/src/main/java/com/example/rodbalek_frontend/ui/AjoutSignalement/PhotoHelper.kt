package com.example.rodbalek_frontend.ui.AjoutSignalement

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

class PhotoHelper(private val context: Context) {

    var photoFile: File? = null

    fun openCamera(launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(intent)
    }

    fun savePhoto(data: Intent?): Bitmap? {
        val bitmap = data?.extras?.get("data") as? Bitmap ?: return null

        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        photoFile = File(context.filesDir, fileName)

        FileOutputStream(photoFile!!).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        return bitmap
    }
}
