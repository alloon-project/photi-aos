package com.photi.aos.view.ui.util

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.photi.aos.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object CameraHelper {
    const val REQUEST_CAMERA_PERMISSION = 1001
    private lateinit var currentPhotoPath: String
    private lateinit var photoUri: Uri

    // 권한 체크 및 요청
    fun checkPermissions(fragment: Fragment, onSuccess: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                fragment.requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                onSuccess()
            }
        } else {
            // Android 6.0 미만에서는 권한 요청 없이 바로 실행
            onSuccess()
        }
    }

    // 파일 생성
    fun createImageFile(fragment: Fragment): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    // 사진 촬영 실행
    fun takePicture(fragment: Fragment, takePictureLauncher: ActivityResultLauncher<Uri>) {
        val photoFile = createImageFile(fragment)
        photoUri = FileProvider.getUriForFile(
            fragment.requireContext(),
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoUri)
    }

    // 현재 사진 경로 반환
    fun getCurrentPhotoPath(): String {
        return currentPhotoPath
    }

    fun getPhotoUri(): Uri {
        return photoUri
    }
}
