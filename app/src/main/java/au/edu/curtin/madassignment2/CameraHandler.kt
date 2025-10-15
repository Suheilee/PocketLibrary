package au.edu.curtin.madassignment2

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraHandler(private val context: Context) {

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.getExternalFilesDir(null), "Pictures/BookCovers")

        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        return File.createTempFile(
            "BOOK_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}

@Composable
fun rememberCameraLauncher(
    onPhotoCaptured: (String) -> Unit
): () -> Unit {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cameraHandler = remember { CameraHandler(context) }
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoPath != null) {
            onPhotoCaptured(currentPhotoPath!!)
        }
    }

    return {
        val photoFile = cameraHandler.createImageFile()
        currentPhotoPath = photoFile.absolutePath
        val photoUri = cameraHandler.getUriForFile(photoFile)
        cameraLauncher.launch(photoUri)
    }
}