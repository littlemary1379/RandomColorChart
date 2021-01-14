package com.mary.kotlinprojectstudy.writing

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.main.MainActivity
import com.mary.kotlinprojectstudy.util.ActivityUtil
import com.mary.kotlinprojectstudy.util.DlogUtil
import org.w3c.dom.Text
import java.util.*

class WritingColorActivity : AppCompatActivity() {

    lateinit var editTextRed: EditText
    lateinit var editTextGreen: EditText
    lateinit var editTextBlue: EditText
    lateinit var viewPreviewColor: View

    lateinit var editTextName: EditText
    lateinit var editTextHEX: EditText
    lateinit var editTextSource: EditText

    lateinit var textViewWrite: TextView

    var lastId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_color)

        checkBundle()

        findView()
        rgbTextWatcher()
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
        viewPreviewColor = findViewById(R.id.viewPreviewColor)

        editTextName = findViewById(R.id.editTextName)
        editTextHEX = findViewById(R.id.editTextHEX)
        editTextSource = findViewById(R.id.editTextSource)

        textViewWrite = findViewById(R.id.textViewWrite)

    }

    private fun rgbTextWatcher() {
        editTextRed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (editTextRed.text.toString() == "") {
                    viewPreviewColor.setBackgroundResource(R.drawable.test_border)
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

                viewPreviewColor.setBackgroundColor(Color.rgb(red, green, blue))

            }

        })

        editTextGreen.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (editTextGreen.text.toString() == "") {
                    viewPreviewColor.setBackgroundResource(R.drawable.test_border)
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

                viewPreviewColor.setBackgroundColor(Color.rgb(red, green, blue))
            }

        })

        editTextBlue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (editTextBlue.text.toString() == "") {
                    viewPreviewColor.setBackgroundResource(R.drawable.test_border)
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

                viewPreviewColor.setBackgroundColor(Color.rgb(red, green, blue))
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