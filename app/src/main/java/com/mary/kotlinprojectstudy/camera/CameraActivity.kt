package com.mary.kotlinprojectstudy.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.util.DlogUtil
import com.mary.kotlinprojectstudy.util.PermissionUtil
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService

class CameraActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraActivity"
    }

    private lateinit var previewView : PreviewView

    private lateinit var imageCapture: ImageCapture

    private lateinit var outputDirectoryStreamException : File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        findView()
        permissionCheck()
    }

    private fun permissionCheck() {

        var permissionList = listOf(Manifest.permission.CAMERA)

        if(!PermissionUtil.checkPermission(this, permissionList)) {
            PermissionUtil.requestPermission(this, permissionList)
        } else {
            openCamera()
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
            openCamera()
        } else {
            DlogUtil.d(TAG, "승인 거부")
            onBackPressed()
        }
    }

    private fun findView(){
        previewView = findViewById(R.id.previewView)
    }

    private fun openCamera(){
        DlogUtil.d(TAG, "openCamera")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider : ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
            } catch (e : Exception) {
                DlogUtil.d(TAG, "바인딩 실패 $e")
            }
        }, ContextCompat.getMainExecutor(this))

    }

}