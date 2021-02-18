package com.mary.kotlinprojectstudy.showdetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.icu.number.IntegerWidth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.larswerkman.holocolorpicker.*
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.ui.NavigationViewHolder
import com.mary.kotlinprojectstudy.util.DlogUtil
import org.w3c.dom.Text
import java.util.*
import java.util.function.DoubleToLongFunction

class ColorDetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ColorDetailActivity"
    }

    private lateinit var textViewColorName: TextView
    private lateinit var textViewHEX: TextView
    private lateinit var textViewRed: TextView
    private lateinit var textViewGreen: TextView
    private lateinit var textViewBlue: TextView
    private lateinit var textViewColorCopy : TextView

    private lateinit var textViewNewHEX: TextView
    private lateinit var textViewNewRed: TextView
    private lateinit var textViewNewGreen: TextView
    private lateinit var textViewNewBlue: TextView
    private lateinit var textViewNewColorCopy : TextView


    private lateinit var frameLayoutNavigation: FrameLayout
    private lateinit var navigationViewHolder: NavigationViewHolder

    private val db = Firebase.firestore

    private lateinit var mainColor: MainColor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_detail)

        checkBundle()
        findView()
        initNavigation()
        setListener()
    }

    private fun checkBundle() {
        var bundle = intent.getBundleExtra("BUNDLE_KEY")
        if (bundle != null) {
            DlogUtil.d(TAG, "번들 : ${bundle.getLong("id")}")
            loadColorById(bundle.getLong("id"))
        }
    }

    private fun findView() {
        frameLayoutNavigation = findViewById(R.id.frameLayoutNavigation)

        textViewColorName = findViewById(R.id.textViewColorName)
        textViewHEX = findViewById(R.id.textViewHEX)
        textViewRed = findViewById(R.id.textViewRed)
        textViewGreen = findViewById(R.id.textViewGreen)
        textViewBlue = findViewById(R.id.textViewBlue)
        textViewColorCopy = findViewById(R.id.textViewColorCopy)

        textViewNewHEX = findViewById(R.id.textViewNewHEX)
        textViewNewRed = findViewById(R.id.textViewNewRed)
        textViewNewGreen = findViewById(R.id.textViewNewGreen)
        textViewNewBlue = findViewById(R.id.textViewNewBlue)
        textViewNewColorCopy = findViewById(R.id.textViewNewColorCopy)
    }

    private fun initNavigation() {
        navigationViewHolder = NavigationViewHolder(this)
        navigationViewHolder.navigationViewHolderDelegate =
            object : NavigationViewHolder.NavigationViewHolderDelegate {
                override fun back() {
                    onBackPressed()
                }
            }

        navigationViewHolder.setTitle("Pick Color")
        frameLayoutNavigation.addView(navigationViewHolder.view)
    }

    private fun loadColorById(id: Long) {
        db.collection("colors").whereEqualTo("id", id).get()
            .addOnSuccessListener {
                run {
                    for (document in it) {
                        DlogUtil.d(TAG, "id 소환 성공. ${document.data}")
                        mainColor = document.toObject(MainColor::class.java)
                        initPicker(mainColor.red, mainColor.green, mainColor.blue)
                        updateView()
                    }
                }
            }
    }

    private fun updateView() {
        textViewColorName.text = "Color Name : ${mainColor.name} / ${mainColor.source}"
        textViewHEX.text = "HEX : #${mainColor.HEX}"
        textViewRed.text = "Red : ${mainColor.red}"
        textViewGreen.text = "Green : ${mainColor.green}"
        textViewBlue.text = "Blue : ${mainColor.blue}"

        textViewNewHEX.text = "HEX : #${mainColor.HEX}"
        textViewNewRed.text = "Red : ${mainColor.red}"
        textViewNewGreen.text = "Green : ${mainColor.green}"
        textViewNewBlue.text = "Blue : ${mainColor.blue}"
    }

    private fun newColorUpdateView(hex: String, red: Int, green: Int, blue: Int) {
        textViewNewHEX.text = "HEX : #$hex"
        textViewNewRed.text = "Red : $red"
        textViewNewGreen.text = "Green : $green"
        textViewNewBlue.text = "Blue : $blue"

    }

    private fun setListener(){
        textViewColorCopy.setOnClickListener {
            var clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val hex = textViewHEX.text.split(":")
            val clip : ClipData = ClipData.newPlainText("pick color", hex[1].trim())
            clipboardManager.setPrimaryClip(clip)

            //Toast.makeText(this, "Copy ${hex[1].trim()}", Toast.LENGTH_LONG).show()
            var layoutInflater = LayoutInflater.from(this).inflate(R.layout.view_holder_toast,null)
            var text : TextView = layoutInflater.findViewById(R.id.textViewToast)
            text.text="Copy ${hex[1].trim()}"

            var toast = Toast(this)
            toast.view = layoutInflater
            toast.show()



        }

        textViewNewColorCopy.setOnClickListener {
            var clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val hex = textViewNewHEX.text.split(":")
            val clip : ClipData = ClipData.newPlainText("pick color", hex[1].trim())
            clipboardManager.setPrimaryClip(clip)
            //Toast.makeText(this, "Copy ${hex[1].trim()}", Toast.LENGTH_LONG).show()

            var layoutInflater = LayoutInflater.from(this).inflate(R.layout.view_holder_toast,null)
            var text : TextView = layoutInflater.findViewById(R.id.textViewToast)
            text.text="Copy ${hex[1].trim()}"

            var toast = Toast(this)
            toast.view = layoutInflater
            toast.show()
        }
    }

    private fun initPicker(red: Int, green: Int, blue: Int) {
        DlogUtil.d(TAG, "??? $red  $green  $blue")
        var picker: ColorPicker = findViewById(R.id.colorPicker)
        var saturationBar: SaturationBar = findViewById(R.id.saturationBar)
        var valueBar: ValueBar = findViewById(R.id.valueBar)

        picker.addSaturationBar(saturationBar)
        picker.addValueBar(valueBar)

        picker.color = Color.rgb(red, green, blue)
        picker.oldCenterColor = Color.rgb(red, green, blue)
        picker.setNewCenterColor(Color.rgb(red, green, blue))

        picker.setOnColorChangedListener {
            DlogUtil.d(TAG, "변경시... $it")
            var hexColor = Integer.toHexString(it)
            if (hexColor.length > 1) {
                hexColor = hexColor.substring(2)
            } else {
                DlogUtil.d(TAG, "뭐야 뭔값인데 ")
            }
            DlogUtil.d(TAG, "hex : $hexColor")
            DlogUtil.d(TAG, "red : ${hexColor.substring(4, 6)}")
            DlogUtil.d(TAG, "red : ${Integer.parseInt(hexColor.substring(4, 6), 16)}")

            newColorUpdateView(
                hexColor.toUpperCase(Locale.ROOT),
                Integer.parseInt(hexColor.substring(0, 2), 16),
                Integer.parseInt(hexColor.substring(2, 4), 16),
                Integer.parseInt(hexColor.substring(4, 6), 16)
            )

        }

    }


}