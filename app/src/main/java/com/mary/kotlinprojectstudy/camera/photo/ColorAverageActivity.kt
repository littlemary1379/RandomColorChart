package com.mary.kotlinprojectstudy.camera.photo

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.showdetail.ColorDetailActivity
import com.mary.kotlinprojectstudy.util.DlogUtil

class ColorAverageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ColorAverageActivity"
    }

    private lateinit var imageViewPreview: ImageView
    private lateinit var imageViewCancel: ImageView
    private lateinit var viewAverage: View

    private var uriString: String? = null
    private var filteredBackgroundHsl: FloatArray? = null

    private val BLACK_MAX_LIGHTNESS = 0.08f
    private val WHITE_MIN_LIGHTNESS = 0.90f
    private val POPULATION_FRACTION_FOR_WHITE_OR_BLACK = 2.5f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_average)

        checkBundle()
        findView()
        setImage()
        setListener()
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
        imageViewCancel = findViewById(R.id.imageViewCancel)
        viewAverage = findViewById(R.id.viewAverage)
    }

    private fun setImage() {
        imageViewPreview.setImageURI(Uri.parse(uriString))

        updateView()

    }

    private fun setListener() {
        imageViewCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setColor(): Int {
        var imageDrawable = imageViewPreview.drawable
        var bitmapDrawable = imageDrawable as BitmapDrawable
        var imageBitmap = bitmapDrawable.bitmap
        var paletteBuilder: Palette.Builder = Palette.from(imageBitmap).setRegion(
            0,
            0,
            imageBitmap.width,
            imageBitmap.height
        )

        var palette = paletteBuilder.generate()

        var dominantSwatch: Palette.Swatch? = palette.dominantSwatch
        if (dominantSwatch == null) {
            filteredBackgroundHsl = null
            return Color.WHITE
        }

        if (!isWhiteOrBlack(dominantSwatch.hsl)) {
            filteredBackgroundHsl = dominantSwatch.hsl
            return dominantSwatch.rgb
        }

        var swatches: List<Palette.Swatch> = palette.swatches
        var highestNonWhitePopulation: Float = -1f
        var second: Palette.Swatch? = null
        for (swatch in swatches) {
            if (swatch !== dominantSwatch && swatch.population > highestNonWhitePopulation && !isWhiteOrBlack(
                    swatch.hsl
                )
            ) {
                second = swatch
                highestNonWhitePopulation = swatch.population.toFloat()
            }
        }

        if (second == null) {
            filteredBackgroundHsl = null
            return dominantSwatch.rgb
        }

        return if (dominantSwatch.population / highestNonWhitePopulation > POPULATION_FRACTION_FOR_WHITE_OR_BLACK) {
            filteredBackgroundHsl = null
            dominantSwatch.rgb
        } else {
            filteredBackgroundHsl = second.hsl
            second.rgb
        }


    }

    private fun isWhiteOrBlack(hsl: FloatArray): Boolean {
        return isBlack(hsl) || isWhite(hsl)
    }

    private fun isBlack(hsl: FloatArray): Boolean {
        return hsl[2] <= BLACK_MAX_LIGHTNESS
    }

    private fun isWhite(hsl: FloatArray): Boolean {
        return hsl[2] <= WHITE_MIN_LIGHTNESS
    }

    private fun updateView() {
        //Average
        DlogUtil.d(TAG, "setColor : ${setColor()}")

        var hexColor = Integer.toHexString(setColor())
        if (hexColor.length > 1) {
            hexColor = hexColor.substring(2)
        } else {
            DlogUtil.d(TAG, "뭐야 뭔값인데 ")
        }
        DlogUtil.d(TAG, "hex : $hexColor")

        viewAverage.setBackgroundColor(
            Color.rgb(
                Integer.parseInt(hexColor.substring(0, 2), 16),
                Integer.parseInt(hexColor.substring(2, 4), 16),
                Integer.parseInt(hexColor.substring(4, 6), 16)
            )
        )

    }


}