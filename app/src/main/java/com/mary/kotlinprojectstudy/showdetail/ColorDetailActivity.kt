package com.mary.kotlinprojectstudy.showdetail

import android.graphics.Color
import android.icu.number.IntegerWidth
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.larswerkman.holocolorpicker.*
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.ui.NavigationViewHolder
import com.mary.kotlinprojectstudy.util.DlogUtil
import java.util.function.DoubleToLongFunction

class ColorDetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ColorDetailActivity"
    }

    lateinit var frameLayoutNavigation: FrameLayout
    lateinit var navigationViewHolder: NavigationViewHolder

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_detail)

        checkBundle()
        findView()
        initNavigation()

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
    }

    private fun initNavigation() {
        navigationViewHolder = NavigationViewHolder(this)
        navigationViewHolder.navigationViewHolderDelegate = object : NavigationViewHolder.NavigationViewHolderDelegate {
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
                        var mainColor : MainColor = document.toObject(MainColor::class.java)
                        initPicker(mainColor.red, mainColor.green, mainColor.blue)
                    }
                }
            }
    }

    private fun initPicker(red : Int, green : Int, blue : Int) {
        DlogUtil.d(TAG, "??? $red  $green  $blue")
        var picker: ColorPicker = findViewById(R.id.colorPicker)
        var svBar: SVBar = findViewById(R.id.svBar)
        var opacityBar: OpacityBar = findViewById(R.id.opacityBar)
        var saturationBar: SaturationBar = findViewById(R.id.saturationBar)
        var valueBar: ValueBar = findViewById(R.id.valueBar)

        picker.addSVBar(svBar)
        picker.addOpacityBar(opacityBar)
        picker.addSaturationBar(saturationBar)
        picker.addValueBar(valueBar)

        picker.color = Color.rgb(red, green, blue)
        picker.oldCenterColor = Color.rgb(red, green, blue)
        picker.setNewCenterColor(Color.rgb(red, green, blue))

        picker.setOnColorChangedListener {
            DlogUtil.d(TAG, "변경시... $it")
            var hexColor = Integer.toHexString(it)
            if(hexColor.length >1 ){
                hexColor = hexColor.substring(2)
            } else {
                DlogUtil.d(TAG, "뭐야 뭔값인데 ")
            }
            DlogUtil.d(TAG, "hex : $hexColor")
        }

    }


}