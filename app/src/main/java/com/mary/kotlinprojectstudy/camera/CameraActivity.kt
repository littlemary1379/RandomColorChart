package com.mary.kotlinprojectstudy.camera

import android.Manifest
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.camera.photo.ColorAverageActivity
import com.mary.kotlinprojectstudy.util.ActivityUtil
import com.mary.kotlinprojectstudy.util.DlogUtil
import com.mary.kotlinprojectstudy.util.PermissionUtil
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CameraActivity"
    }

    private lateinit var previewView: PreviewView
    private lateinit var imageViewPhoto: ImageView
    private lateinit var frameLayoutShutter: FrameLayout
    private lateinit var frameLayoutPreview: FrameLayout
    private lateinit var imageViewPreview: ImageView
    private lateinit var imageViewCancel: ImageView
    private lateinit var imageViewAverage: ImageView

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraAnimationListener: Animation.AnimationListener

    private var savedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        findView()
        permissionCheck()
        setListener()
        setCameraAnimationListener()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun permissionCheck() {

        var permissionList =
            listOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!PermissionUtil.checkPermission(this, permissionList)) {
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

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            DlogUtil.d(TAG, "승인")
            openCamera()
        } else {
            DlogUtil.d(TAG, "승인 거부")
            onBackPressed()
        }
    }

    private fun findView() {
        previewView = findViewById(R.id.previewView)
        imageViewPhoto = findViewById(R.id.imageViewPhoto)
        frameLayoutShutter = findViewById(R.id.frameLayoutShutter)
        imageViewPreview = findViewById(R.id.imageViewPreview)
        frameLayoutPreview = findViewById(R.id.frameLayoutPreview)
        imageViewCancel = findViewById(R.id.imageViewCancel)
        imageViewAverage = findViewById(R.id.imageViewAverage)
    }

    private fun setListener() {
        imageViewPhoto.setOnClickListener {
            savePhoto()
        }

        imageViewCancel.setOnClickListener {
            if(frameLayoutPreview.visibility == View.VISIBLE) {
                hideCaptureImage()
            }
        }

        imageViewAverage.setOnClickListener {
            hideCaptureImage()
            var bundle = Bundle()
            bundle.putString("imageUri", savedUri.toString())
            ActivityUtil.startActivityWithoutFinish(this, ColorAverageActivity::class.java, bundle)
        }

    }


    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun openCamera() {
        DlogUtil.d(TAG, "openCamera")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                DlogUtil.d(TAG, "바인딩 성공")

            } catch (e: Exception) {
                DlogUtil.d(TAG, "바인딩 실패 $e")
            }
        }, ContextCompat.getMainExecutor(this))

    }

    private fun savePhoto() {
        imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("yy-mm-dd", Locale.US).format(System.currentTimeMillis()) + ".png"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    DlogUtil.d(TAG, "savedUri : $savedUri")

                    val animation =
                        AnimationUtils.loadAnimation(this@CameraActivity, R.anim.camera_shutter)
                    animation.setAnimationListener(cameraAnimationListener)
                    frameLayoutShutter.animation = animation
                    frameLayoutShutter.visibility = View.VISIBLE
                    frameLayoutShutter.startAnimation(animation)


                    DlogUtil.d(TAG, "imageCapture")
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onBackPressed()
                }

            })

    }

    private fun setCameraAnimationListener() {
        cameraAnimationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                frameLayoutShutter.visibility = View.GONE
                showCaptureImage()
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        }
    }

    private fun showCaptureImage(): Boolean {
        if (frameLayoutPreview.visibility == View.GONE) {
            frameLayoutPreview.visibility = View.VISIBLE
            imageViewPreview.setImageURI(savedUri)
            return false
        }

        return true

    }

    private fun hideCaptureImage() {
        imageViewPreview.setImageURI(null)
        frameLayoutPreview.visibility = View.GONE

    }

    override fun onBackPressed() {
        if (showCaptureImage()) {
            DlogUtil.d(TAG, "CaptureImage true")
            hideCaptureImage()
        } else {
            onBackPressed()
            DlogUtil.d(TAG, "CaptureImage false")

        }
    }

}