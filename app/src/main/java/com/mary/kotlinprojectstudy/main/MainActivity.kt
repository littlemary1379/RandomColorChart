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

    private lateinit var recyclerView: RecyclerView
    lateinit var imageViewWrite: ImageView

    private val db = Firebase.firestore
    var lastId: Int = 0

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

        mainColorAdapter.mainColorAdapterDelegate = object : MainColorAdapter.MainColorAdapterDelegate {
            override fun loadMore() {
                loadMoreColor()
            }

        }

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

                0, 6 -> {
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

    private fun loadId() {
        db.collection("colors").orderBy("id", Query.Direction.DESCENDING).limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    DlogUtil.d(TAG, "${document.id} => ${document.data}")
                    val lastColor = document.toObject(MainColor::class.java)
                    DlogUtil.d(TAG, lastColor.id)
                    lastId = lastColor.id.toInt()

                    loadColor()

                }
            }.addOnFailureListener { e ->
                DlogUtil.d(TAG, e)
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun loadColor() {

        var list : MutableList<MainColor> = mutableListOf()

        db.collection("colors").orderBy("id").limit(9).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    DlogUtil.d(TAG, "${document.id} => ${document.data}")
                    list.add(document.toObject(MainColor::class.java))
                }

                mainColorAdapter.reloadItem(list.shuffled())
            }
    }

    private fun loadMoreColor() {

        DlogUtil.d(TAG, "???????")

        var list : MutableList<MainColor> = mutableListOf()

        db.collection("colors").orderBy("id").whereGreaterThan("id",mainColorAdapter.itemCount).limit(9).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    DlogUtil.d(TAG, "${document.id} => ${document.data}")
                    list.add(document.toObject(MainColor::class.java))
                }

                mainColorAdapter.loadMoreList(list.shuffled())
            }
    }
}
