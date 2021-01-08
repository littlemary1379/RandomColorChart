package com.mary.kotlinprojectstudy.main

import android.os.Bundle
import android.widget.ImageView
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.main.adapter.MainColorAdapter
import com.mary.kotlinprojectstudy.ui.SpanSize
import com.mary.kotlinprojectstudy.ui.SpannedGridLayoutManager
import com.mary.kotlinprojectstudy.ui.exception.SpaceItemDecorator
import com.mary.kotlinprojectstudy.util.ActivityUtil
import com.mary.kotlinprojectstudy.util.DlogUtil
import com.mary.kotlinprojectstudy.writing.WritingColorActivity

class MainActivity : AppCompatActivity() {

    lateinit var mainColorAdapter: MainColorAdapter

    private lateinit var recyclerView : RecyclerView
    lateinit var imageViewWrite : ImageView

    private val db = Firebase.firestore
    var lastId : Int = 0

    private val list: MutableList<MainColor> = listOf(
        MainColor(1, "Primrose Yellow", 246, 210, 88,"11","22"),
        MainColor(2, "Pale Dogwood", 239, 206, 197,"11","22"),
        MainColor(3, "Hazelnut", 209, 175, 148,"11","22"),
        MainColor(4, "Island Paradise", 151, 213, 224,"11","22"),
        MainColor(5, "Greenery", 136, 177, 75,"11","22"),
        MainColor(6, "Flame", 239, 86, 45,"11","22"),
        MainColor(7, "Pink Yarrow", 209, 48, 118,"11","22"),
        MainColor(8, "Niagara", 85, 135, 162,"11","22"),
        MainColor(9, "Kale", 92, 113, 72,"11","22"),
        MainColor(10, "Lapis Blue", 12, 76, 138,"11","22")
    ) as MutableList<MainColor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findView()
        setListener()
        initRecyclerView()
        loadId()
    }

    private fun findView() {
        recyclerView = findViewById(R.id.recyclerView)
        imageViewWrite = findViewById(R.id.imageViewWrite)
    }

    private fun setListener() {
        imageViewWrite.setOnClickListener {
            DlogUtil.d(TAG, "색 추가 클릭")
            var bundle = Bundle()
            bundle.putInt("lastId", lastId)
            ActivityUtil.startActivityWithoutFinish(this, WritingColorActivity::class.java, bundle)

        }
    }

    private fun initRecyclerView() {

        mainColorAdapter = MainColorAdapter()
        mainColorAdapter.list = list.shuffled()

        val layoutManager = SpannedGridLayoutManager(
            orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
            spans = 6
        )
        layoutManager.itemOrderIsStable = true
        recyclerView.addItemDecoration(SpaceItemDecorator(left = 5, top = 5, right = 5, bottom = 5))

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = mainColorAdapter

        layoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->

            when (position % 9) {

                0 ,6 -> {
                    SpanSize(4, 4)
                }

                3 -> {
                    SpanSize(2, 1)
                }

                4 -> {
                    SpanSize(4, 1)
                }

                7, 8 -> {
                    SpanSize(1, 2)
                }

                else -> {
                    SpanSize(2, 2)
                }
            }

        }


    }

    private fun loadId(){
        db.collection("colors").orderBy("id", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    DlogUtil.d(TAG, "${document.id} => ${document.data}")
                    val lastColor = document.toObject(MainColor::class.java)
                    DlogUtil.d(TAG, lastColor.id)
                    lastId = lastColor.id.toInt()

                }
            }.addOnFailureListener { e ->
                DlogUtil.d(TAG, e)
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }


}