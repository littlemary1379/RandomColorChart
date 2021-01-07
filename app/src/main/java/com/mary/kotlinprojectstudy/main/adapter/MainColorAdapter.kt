package com.mary.kotlinprojectstudy.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.main.adapter.item.MainColorViewHolder

class MainColorAdapter : RecyclerView.Adapter<MainColorViewHolder>() {

    var list : List<MainColor> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_color_chart, parent, false)
        return MainColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainColorViewHolder, position: Int) {
        val item = list[position]

//        var width = 1
//        var height = 1
//
//        if(position == 0) {
//            width = 2
//            height = 2
//        }
//
//
//
//        holder.itemView.layoutParams = SpanLayoutParams(SpanSize(width, height))

        holder.apply {
            updateView(item)
        }

    }

//    override fun onViewAttachedToWindow(holder: MainColorViewHolder) {
//        super.onViewAttachedToWindow(holder)
//        var layoutParam : ViewGroup.LayoutParams = holder.itemView.layoutParams
//        if(layoutParam != null && layoutParam is StaggeredGridLayoutManager.LayoutParams && holder.layoutPosition == 0) {
//            var gridLayoutParam : StaggeredGridLayoutManager.LayoutParams = layoutParam
//        }
//        holder.setIsRecyclable(false)
//    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: " + list.size)
        return list.size
    }

    companion object {
        private const val TAG = "MainColorAdapter"
    }
}