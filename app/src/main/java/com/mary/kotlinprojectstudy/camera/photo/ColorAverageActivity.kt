package com.mary.kotlinprojectstudy.camera.photo

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.util.DlogUtil
import kotlin.math.abs

class ColorAverageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ColorAverageActivity"
    }

    private lateinit var imageViewPreview: ImageView
    private lateinit var imageViewCancel: ImageView
    private lateinit var viewAverage: View
    private lateinit var viewColor1 : View

    private var uriString: String? = null

    private var filteredBackgroundHsl: FloatArray? = null
    private var imageBitmap: Bitmap? = null

    private val BLACK_MAX_LIGHTNESS = 0.08f
    private val WHITE_MIN_LIGHTNESS = 0.90f
    private val POPULATION_FRACTION_FOR_WHITE_OR_BLACK = 2.5f
    private var textColorStartWidthFraction = 0.4f
    private val POPULATION_FRACTION_FOR_DOMINANT = 0.01f
    private val MIN_SATURATION_WHEN_DECIDING = 0.19f
    private val POPULATION_FRACTION_FOR_MORE_VIBRANT = 1.0f
    private val RESIZE_BITMAP_AREA = 150 * 150
    private val MINIMUM_IMAGE_FRACTION = 0.002


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
        viewColor1 = findViewById(R.id.viewColor1)
    }

    private fun setImage() {
        imageViewPreview.setImageURI(Uri.parse(uriString))

        setBitmap()
        updateView()
    }

    private fun setListener() {
        imageViewCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setBitmap() {
        var imageDrawable = imageViewPreview.drawable
        var bitmapDrawable = imageDrawable as BitmapDrawable
        imageBitmap = bitmapDrawable.bitmap
    }

    private fun setBackgroundColor(): Int {

        if (imageBitmap == null) {
            return 0

        } else {

            var paletteBuilder: Palette.Builder = Palette.from(imageBitmap!!).setRegion(
                0,
                0,
                imageBitmap!!.width,
                imageBitmap!!.height
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


    }

    private fun setFirstColor(): Int {
        var backgroundColor: Int = setBackgroundColor()

        if (imageBitmap == null) {
            return 0
        } else {

            var paletteBuilder: Palette.Builder = Palette.from(imageBitmap!!).setRegion(
                (imageBitmap!!.width * textColorStartWidthFraction).toInt(),
                0,
                imageBitmap!!.width,
                imageBitmap!!.height
            )

            if (filteredBackgroundHsl != null) {
                paletteBuilder.addFilter(Palette.Filter { rgb, hsl ->
                    var diff: Float = abs(hsl[0] - filteredBackgroundHsl!![0])
                    return@Filter diff > 10 && diff < 350
                })
            }

            paletteBuilder.addFilter(blackWhiteFilter)
            var palette = paletteBuilder.generate()

            var firstColor = selectDarkForegroundColor(backgroundColor, palette)

            DlogUtil.d(TAG, firstColor)

            return firstColor

        }

    }

    private fun updateView() {
        //Average
        DlogUtil.d(TAG, "setColor : ${setBackgroundColor()}")
        DlogUtil.d(TAG, "setFirstColor : ${setFirstColor()}")

        var backgroundHexColor = Integer.toHexString(setBackgroundColor())
        if (backgroundHexColor.length > 1) {
            backgroundHexColor = backgroundHexColor.substring(2)
        } else {
            DlogUtil.d(TAG, "뭐야 뭔값인데 ")
        }
        DlogUtil.d(TAG, "hex : $backgroundHexColor")

        viewAverage.setBackgroundColor(
            Color.rgb(
                Integer.parseInt(backgroundHexColor.substring(0, 2), 16),
                Integer.parseInt(backgroundHexColor.substring(2, 4), 16),
                Integer.parseInt(backgroundHexColor.substring(4, 6), 16)
            )
        )

        var Color1HexColor = Integer.toHexString(setBackgroundColor())
        if (Color1HexColor.length > 1) {
            Color1HexColor = Color1HexColor.substring(2)
        } else {
            DlogUtil.d(TAG, "뭐야 뭔값인데 ")
        }
        DlogUtil.d(TAG, "hex : $Color1HexColor")

        viewColor1.setBackgroundColor(
            Color.rgb(
                Integer.parseInt(Color1HexColor.substring(0, 2), 16),
                Integer.parseInt(Color1HexColor.substring(2, 4), 16),
                Integer.parseInt(Color1HexColor.substring(4, 6), 16)
            )
        )


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

    private var blackWhiteFilter: Palette.Filter = Palette.Filter { _, hsl ->
        isWhiteOrBlack(hsl)
    }


    private fun selectDarkForegroundColor(backgroundColor: Int, palette: Palette): Int {
//        return if (NotificationColorUtil.isColorLight(backgroundColor)) {
//            selectForegroundColorForSwatches(
//                palette.darkVibrantSwatch,
//                palette.vibrantSwatch,
//                palette.darkMutedSwatch,
//                palette.mutedSwatch,
//                palette.dominantSwatch,
//                Color.BLACK
//            )
//        } else {
//            selectForegroundColorForSwatches(
//                palette.lightVibrantSwatch,
//                palette.vibrantSwatch,
//                palette.lightMutedSwatch,
//                palette.mutedSwatch,
//                palette.dominantSwatch,
//                Color.WHITE
//            )
//        }

        return selectForegroundColorForSwatches(
            palette.darkVibrantSwatch,
            palette.vibrantSwatch,
            palette.darkMutedSwatch,
            palette.mutedSwatch,
            palette.dominantSwatch,
            Color.BLACK
        )
    }

    private fun selectForegroundColorForSwatches(
        moreVibrant: Swatch?,
        vibrant: Swatch?, moreMutedSwatch: Swatch?, mutedSwatch: Swatch?,
        dominantSwatch: Swatch?, fallbackColor: Int
    ): Int {
        var coloredCandidate: Swatch? = let {
            if (moreVibrant != null) {
                return@let vibrant?.let { it1 -> selectVibrantCandidate(moreVibrant, it1) }
            } else {
                return@let null
            }
        }

        if (coloredCandidate == null) {
            coloredCandidate = let {
                if (mutedSwatch != null) {
                    return@let moreMutedSwatch?.let { it1 ->
                        selectMutedCandidate(mutedSwatch, it1)
                    }
                } else {
                    return@let null
                }

            }
        }
        return if (coloredCandidate != null) {
            if (dominantSwatch === coloredCandidate) {
                coloredCandidate.rgb
            } else if (coloredCandidate.population.toFloat() / dominantSwatch!!.population
                < POPULATION_FRACTION_FOR_DOMINANT
                && dominantSwatch.hsl[1] > MIN_SATURATION_WHEN_DECIDING
            ) {
                dominantSwatch.rgb
            } else {
                coloredCandidate.rgb
            }
        } else if (hasEnoughPopulation(dominantSwatch)) {
            dominantSwatch!!.rgb
        } else {
            fallbackColor
        }
    }

    private fun selectVibrantCandidate(
        first: Swatch,
        second: Swatch
    ): Swatch? {
        val firstValid: Boolean = hasEnoughPopulation(first)
        val secondValid: Boolean = hasEnoughPopulation(second)
        if (firstValid && secondValid) {
            val firstPopulation = first.population
            val secondPopulation = second.population
            return if (firstPopulation / secondPopulation < POPULATION_FRACTION_FOR_MORE_VIBRANT) {
                second
            } else {
                first
            }
        } else if (firstValid) {
            return first
        } else if (secondValid) {
            return second
        }
        return null
    }

    private fun selectMutedCandidate(
        first: Swatch,
        second: Swatch
    ): Swatch? {
        val firstValid: Boolean = hasEnoughPopulation(first)
        val secondValid: Boolean = hasEnoughPopulation(second)
        if (firstValid && secondValid) {
            val firstSaturation = first.hsl[1]
            val secondSaturation = second.hsl[1]
            val populationFraction = first.population / second.population.toFloat()
            return if (firstSaturation * populationFraction > secondSaturation) {
                first
            } else {
                second
            }
        } else if (firstValid) {
            return first
        } else if (secondValid) {
            return second
        }
        return null
    }

    private fun hasEnoughPopulation(swatch: Swatch?): Boolean {
        // We want a fraction that is at least 1% of the image
        return (swatch != null
                && swatch.population / RESIZE_BITMAP_AREA > MINIMUM_IMAGE_FRACTION)
    }

}