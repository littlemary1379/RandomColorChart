package com.mary.kotlinprojectstudy.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.main.MainActivity
import com.mary.kotlinprojectstudy.main.adapter.item.MainColorViewHolder
import com.mary.kotlinprojectstudy.util.DlogUtil

class MainColorAdapter : RecyclerView.Adapter<MainColorViewHolder>() {

    interface MainColorAdapterDelegate {
        fun loadMore()
    }

    var list : MutableList<MainColor> = mutableListOf()
    lateinit var mainColorAdapterDelegate : MainColorAdapterDelegate

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_color_chart, parent, false)
        return MainColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainColorViewHolder, position: Int) {
        val item = list[position]

        holder.apply {
            updateView(item)
        }

        if(position >= itemCount -1 ) {
            DlogUtil.d(TAG, "???????")
            mainColorAdapterDelegate.loadMore()
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: " + list.size)
        return list.size
    }

    fun reloadItem(mainColors : List<MainColor>){
        list.clear()
        list.addAll(mainColors)
        notifyDataSetChanged()
    }

    fun loadMoreList(mainColors: List<MainColor>) {
        list.addAll(mainColors)
        notifyItemRangeChanged(itemCount, mainColors.size)
    }

    companion object {
        private const val TAG = "MainColorAdapter"
    }
}