package com.mary.kotlinprojectstudy.main

import android.os.Bundle
import android.widget.ImageView
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.main.adapter.MainColorAdapter
import com.mary.kotlinprojectstudy.ui.SpanSize
import com.mary.kotlinprojectstudy.ui.SpannedGridLayoutManager
import com.mary.kotlinprojectstudy.ui.exception.SpaceItemDecorator
import com.mary.kotlinprojectstudy.util.DlogUtil

class MainActivity : AppCompatActivity() {

    lateinit var mainColorAdapter: MainColorAdapter

    private lateinit var recyclerView : RecyclerView
    lateinit var imageViewWrite : ImageView

    private val list: MutableList<MainColor> = listOf(
        MainColor(1, "Primrose Yellow", 246, 210, 88),
        MainColor(2, "Pale Dogwood", 239, 206, 197),
        MainColor(3, "Hazelnut", 209, 175, 148),
        MainColor(4, "Island Paradise", 151, 213, 224),
        MainColor(5, "Greenery", 136, 177, 75),
        MainColor(6, "Flame", 239, 86, 45),
        MainColor(7, "Pink Yarrow", 209, 48, 118),
        MainColor(8, "Niagara", 85, 135, 162),
        MainColor(9, "Kale", 92, 113, 72),
        MainColor(10, "Lapis Blue", 12, 76, 138)
    ) as MutableList<MainColor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findView()
        setListener()
        initRecyclerView()
    }

    private fun findView() {
        recyclerView = findViewById(R.id.recyclerView)
        imageViewWrite = findViewById(R.id.imageViewWrite)
    }

    private fun setListener() {
        imageViewWrite.setOnClickListener {
            DlogUtil.d(TAG, "색 추가 클릭")
        }
    }

    private fun initRecyclerView() {

        mainColorAdapter = MainColorAdapter()
        mainColorAdapter.list = list.shuffled()
//        val layoutManager = GridLayoutManager(this, 6)
//        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                var gridPosition = position % 5
//                when (gridPosition) {
//                    0 -> {
//                        return 4
//                    }
//                    1 -> {
//                        return 2
//                    }
//                    2 -> {
//                        return 2
//                    }
//                    3 -> {
//                        return 4
//                    }
//                    4 -> {
//                        return 2
//                    }
//                    5 -> {
//                        return 2
//                    }
//                }
//
//                return 0;
//            }
//
//        }

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

    companion object {
        private const val TAG = "MainActivity"
    }


}