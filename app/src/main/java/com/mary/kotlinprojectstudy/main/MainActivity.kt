package com.mary.kotlinprojectstudy.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.camera.CameraActivity
import com.mary.kotlinprojectstudy.main.adapter.MainColorAdapter
import com.mary.kotlinprojectstudy.showdetail.ColorDetailActivity
import com.mary.kotlinprojectstudy.ui.SpanSize
import com.mary.kotlinprojectstudy.ui.SpannedGridLayoutManager
import com.mary.kotlinprojectstudy.ui.exception.SpaceItemDecorator
import com.mary.kotlinprojectstudy.util.ActivityUtil
import com.mary.kotlinprojectstudy.util.DlogUtil
import com.mary.kotlinprojectstudy.util.EventUtil
import com.mary.kotlinprojectstudy.writing.WritingColorActivity

class MainActivity : AppCompatActivity() {

    lateinit var mainColorAdapter: MainColorAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageViewAdd: ImageView
    private lateinit var imageViewWrite: ImageView
    private lateinit var imageViewPhoto: ImageView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val db = Firebase.firestore
    var add: Boolean = true
    var lastId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initEvent()
        findView()
        setListener()
        initRecyclerView()
        loadId()
    }

    private fun initEvent() {
        EventUtil.addEventObserver("sendColor", this, object : EventUtil.EventRunnable {
            override fun run(arrow: String, poster: Any, data: HashMap<String, Any>?) {
                DlogUtil.d(TAG, "옵저버 됐나??????")
                if (!data.isNullOrEmpty()) {
                    DlogUtil.d(TAG, "헉미친 안빔")
                    var id: Long = data["id"].toString().toLong()
                    var bundle = Bundle()
                    bundle.putLong("id", id)
                    ActivityUtil.startActivityWithoutFinish(
                        this@MainActivity,
                        ColorDetailActivity::class.java,
                        bundle
                    )
                } else {
                    DlogUtil.d(TAG, "비었니.......?")
                }
            }
        })
    }

    private fun findView() {
        recyclerView = findViewById(R.id.recyclerView)
        imageViewAdd = findViewById(R.id.imageViewAdd)
        imageViewWrite = findViewById(R.id.imageViewWrite)
        imageViewPhoto = findViewById(R.id.imageViewPhoto)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    }

    private fun setListener() {
        imageViewWrite.setOnClickListener {
            DlogUtil.d(TAG, "색 추가 클릭")
            var bundle = Bundle()
            bundle.putInt("lastId", lastId)
            ActivityUtil.startActivityWithoutFinish(this, WritingColorActivity::class.java, bundle)
        }

        imageViewPhoto.setOnClickListener {
            ActivityUtil.startActivityWithoutFinish(this, CameraActivity::class.java)
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            loadColor()
        }

        imageViewAdd.setOnClickListener {
            DlogUtil.d(TAG, "메뉴 클릭")
            popupMenu()

        }
    }

    private fun initRecyclerView() {

        mainColorAdapter = MainColorAdapter()

        mainColorAdapter.mainColorAdapterDelegate =
            object : MainColorAdapter.MainColorAdapterDelegate {
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

    private fun popupMenu() {

        var px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, this.resources.displayMetrics)

        if (add) {

            var animation = AnimationUtils.loadAnimation(this, R.anim.rotate_plus_to_close)
            imageViewAdd.animation = animation
            imageViewAdd.startAnimation(animation)
            var writeAnimator = ObjectAnimator.ofFloat(imageViewWrite, "translationY", 0f, -px)
            writeAnimator.duration = 400
            writeAnimator.interpolator = OvershootInterpolator()
            writeAnimator.target = imageViewWrite
            writeAnimator.start()

            var photoAnimator = ObjectAnimator.ofFloat(imageViewPhoto, "translationY", 0f, -px*2)
            photoAnimator.duration = 400
            photoAnimator.interpolator = OvershootInterpolator()
            photoAnimator.target = imageViewPhoto
            photoAnimator.start()

            add = !add
        } else {
            var animation = AnimationUtils.loadAnimation(this, R.anim.rotate_close_to_plus)
            imageViewAdd.animation = animation
            imageViewAdd.startAnimation(animation)

            var writeAnimator = ObjectAnimator.ofFloat(imageViewWrite, "translationY", -px, 0f)
            writeAnimator.duration = 400
            writeAnimator.interpolator = OvershootInterpolator()
            writeAnimator.target = imageViewWrite
            writeAnimator.start()

            var photoAnimator = ObjectAnimator.ofFloat(imageViewPhoto, "translationY", -px*2, 0f)
            photoAnimator.duration = 400
            photoAnimator.interpolator = OvershootInterpolator()
            photoAnimator.target = imageViewPhoto
            photoAnimator.start()

            add = !add
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

        var list: MutableList<MainColor> = mutableListOf()

        db.collection("colors").orderBy("id").limit(18).get()
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

        var list: MutableList<MainColor> = mutableListOf()

        db.collection("colors").orderBy("id").whereGreaterThan("id", mainColorAdapter.itemCount)
            .limit(18).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    DlogUtil.d(TAG, "${document.id} => ${document.data}")
                    list.add(document.toObject(MainColor::class.java))
                }

                mainColorAdapter.loadMoreList(list.shuffled())
            }
    }
}
