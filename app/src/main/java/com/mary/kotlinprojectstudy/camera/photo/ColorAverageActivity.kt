package com.mary.kotlinprojectstudy.camera.photo

import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.mary.kotlinprojectstudy.R

class ColorAverageActivity : AppCompatActivity() {

    private lateinit var imageViewPreview : ImageView

    private var uriString : String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_average)

        checkBundle()
        findView()
        setImage()
    }

    private fun checkBundle() {
        var bundle = intent.getBundleExtra("BUNDLE_KEY")
        if (bundle != null) {
            uriString = bundle.getString("imageUri")

        } else {
            onBackPressed()
        }
    }

    private fun findView() {
        imageViewPreview = findViewById(R.id.imageViewPreview)
    }

    private fun setImage() {
        imageViewPreview.setImageURI(Uri.parse(uriString))

    }

}