package com.mary.kotlinprojectstudy.main.adapter.item


import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor

class MainColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view : View = itemView
    var viewColor : View = view.findViewById(R.id.viewColor)

    fun updateView(item : MainColor) {

        viewColor.setBackgroundColor(Color.rgb(item.red,item.green,item.blue))
    }

}