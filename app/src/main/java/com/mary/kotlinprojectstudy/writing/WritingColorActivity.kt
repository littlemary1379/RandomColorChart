package com.mary.kotlinprojectstudy.writing

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.main.MainActivity
import com.mary.kotlinprojectstudy.ui.NavigationViewHolder
import com.mary.kotlinprojectstudy.util.ActivityUtil
import com.mary.kotlinprojectstudy.util.DlogUtil
import java.lang.StringBuilder
import java.util.*

class WritingColorActivity : AppCompatActivity() {

    lateinit var editTextRed: EditText
    lateinit var editTextGreen: EditText
    lateinit var editTextBlue: EditText
    lateinit var viewPreviewColorRgb: View

    lateinit var editTextName: EditText
    lateinit var editTextHEX: EditText
    lateinit var viewPreviewColorHex : View
    lateinit var editTextSource: EditText

    lateinit var textViewWrite: TextView

    lateinit var frameLayoutNavigation: FrameLayout
    lateinit var navigationViewHolder: NavigationViewHolder

    var lastId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_color)

        checkBundle()

        findView()
        initNavigation()
        rgbTextWatcher()
        hexTextWatcher()
        setListener()
    }

    private fun checkBundle() {
        val bundle = intent.getBundleExtra("BUNDLE_KEY")
        val lastId = bundle?.getInt("lastId")
        if (lastId != null) {
            DlogUtil.d(TAG, lastId)
            this.lastId = lastId
        }
    }

    private fun findView() {
        editTextRed = findViewById(R.id.editTextRed)
        editTextGreen = findViewById(R.id.editTextGreen)
        editTextBlue = findViewById(R.id.editTextBlue)
        viewPreviewColorRgb = findViewById(R.id.viewPreviewColorRgb)

        editTextName = findViewById(R.id.editTextName)
        editTextHEX = findViewById(R.id.editTextHEX)
        viewPreviewColorHex = findViewById(R.id.viewPreviewColorHex)
        editTextSource = findViewById(R.id.editTextSource)

        textViewWrite = findViewById(R.id.textViewWrite)

        frameLayoutNavigation = findViewById(R.id.frameLayoutNavigation)

    }

    private fun initNavigation() {
        navigationViewHolder = NavigationViewHolder(this)
        navigationViewHolder.navigationViewHolderDelegate = object : NavigationViewHolder.NavigationViewHolderDelegate {
            override fun back() {
                onBackPressed()
            }
        }

        navigationViewHolder.setTitle("Writing Color")
        frameLayoutNavigation.addView(navigationViewHolder.view)
    }

    private fun rgbTextWatcher() {
        editTextRed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (editTextRed.text.toString() == "") {
                    viewPreviewColorRgb.setBackgroundResource(R.drawable.test_border)
                    return
                }

                val red: Int = Integer.parseInt(editTextRed.text.toString())
                if (red > 255) {
                    editTextRed.setText(editTextRed.text.substring(0, editTextRed.length() - 1))
                    editTextRed.setSelection(editTextRed.length())
                }

                if (editTextBlue.text.toString() == "" || editTextGreen.text.toString() == "") {
                    return
                }

                val green: Int = Integer.parseInt(editTextGreen.text.toString())
                val blue: Int = Integer.parseInt(editTextBlue.text.toString())

                viewPreviewColorRgb.setBackgroundColor(Color.rgb(red, green, blue))

            }

        })

        editTextGreen.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (editTextGreen.text.toString() == "") {
                    viewPreviewColorRgb.setBackgroundResource(R.drawable.test_border)
                    return
                }

                val green: Int = Integer.parseInt(editTextGreen.text.toString())
                if (green > 255) {
                    editTextGreen.setText(
                        editTextGreen.text.substring(
                            0,
                            editTextGreen.length() - 1
                        )
                    )
                    editTextGreen.setSelection(editTextGreen.length())
                }


                if (editTextRed.text.toString() == "" || editTextBlue.text.toString() == "") {
                    return
                }

                val red: Int = Integer.parseInt(editTextRed.text.toString())

                val blue: Int = Integer.parseInt(editTextBlue.text.toString())

                viewPreviewColorRgb.setBackgroundColor(Color.rgb(red, green, blue))
            }

        })

        editTextBlue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (editTextBlue.text.toString() == "") {
                    viewPreviewColorRgb.setBackgroundResource(R.drawable.test_border)
                    return
                }

                val blue: Int = Integer.parseInt(editTextBlue.text.toString())
                if (blue > 255) {
                    editTextBlue.setText(editTextBlue.text.substring(0, editTextBlue.length() - 1))
                    editTextBlue.setSelection(editTextBlue.length())
                }

                if (editTextRed.text.toString() == "" || editTextGreen.text.toString() == "") {
                    return
                }

                val red: Int = Integer.parseInt(editTextRed.text.toString())
                val green: Int = Integer.parseInt(editTextGreen.text.toString())

                viewPreviewColorRgb.setBackgroundColor(Color.rgb(red, green, blue))
            }

        })
    }

    private fun hexTextWatcher() {
        editTextHEX.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (editTextHEX.text.toString().length<6) {
                    viewPreviewColorHex.setBackgroundResource(R.drawable.test_border)
                    return
                }

                if (editTextHEX.text.toString().length>6) {
                    editTextHEX.setText(editTextHEX.text.substring(0, editTextHEX.length() - 1))
                    editTextHEX.setSelection(editTextHEX.length())
                    return
                }

                val hex : String = editTextHEX.text.toString().trim()
                DlogUtil.d(TAG, hex)

                if(hex.contains("#")) {
                    editTextHEX.setText(editTextHEX.text.substring(1, editTextHEX.length()))
                    editTextHEX.setSelection(editTextHEX.length())
                    return
                }

                var charString : MutableList<String> = mutableListOf()
                charString.clear()
                charString.addAll(hex.split(""))

                val sb = StringBuilder()
                for (i : Int in charString.indices) {
                    if(i == 0 || i== 7) {
                        continue
                    } else {
                        if(charString[i] in "0".."9" || charString[i] in "a".."f" || charString[i] in "A".."F") {
                            sb.append(charString[i])
                        } else {
                            DlogUtil.d(TAG, "16진수 이상 값 입력")
                            Toast.makeText(this@WritingColorActivity, "16진수 범위 안의 값을 사용할 수 없습니다.", Toast.LENGTH_SHORT)
                            return
                        }

                    }
                }

                val finalHex : String = "#$sb"
                DlogUtil.d(TAG, finalHex)

                viewPreviewColorHex.setBackgroundColor(Color.parseColor(finalHex))
            }

        })
    }

    private fun setListener() {
        textViewWrite.setOnClickListener {
            writeColorDB()
        }
    }

    private fun writeColorDB() {
        DlogUtil.d(TAG, "클릭클릭")
        val db = Firebase.firestore

        val color = hashMapOf(
            "id" to lastId + 1,
            "name" to editTextName.text.toString(),
            "red" to editTextRed.text.toString().toInt(),
            "green" to editTextGreen.text.toString().toInt(),
            "blue" to editTextBlue.text.toString().toInt(),
            "HEX" to editTextHEX.text.toString().toUpperCase(Locale.ROOT),
            "source" to editTextSource.text.toString()
        )

        db.collection("colors")
            .add(color)
            .addOnSuccessListener { documentReference ->
                DlogUtil.d(TAG, "add : ${documentReference.id}")
                lastId += 1
                ActivityUtil.startActivityWithFinish(this, MainActivity::class.java)
            }
            .addOnFailureListener { e ->
                DlogUtil.d(TAG, e)
            }
    }


    companion object {
        private const val TAG = "WritingColorActivity"
    }
}