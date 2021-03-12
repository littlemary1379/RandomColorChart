package com.mary.kotlinprojectstudy.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.util.DlogUtil
import com.mary.kotlinprojectstudy.util.PermissionUtil
import java.io.File
import java.util.concurrent.ExecutorService

class CameraActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraActivity"
    }

    private lateinit var imageCapture: ImageCapture

    private lateinit var outputDirectoryStreamException : File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        permissionCheck()
    }

    private fun permissionCheck() {

        var permissionList = listOf(Manifest.permission.CAMERA)

        if(!PermissionUtil.checkPermission(this, permissionList)) {
            PermissionUtil.requestPermission(this, permissionList)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            DlogUtil.d(TAG, "승인")
        } else {
            DlogUtil.d(TAG, "승인 거부")
            onBackPressed()
        }
    }

}