package com.mary.kotlinprojectstudy.main.adapter.item


import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mary.kotlinprojectstudy.R
import com.mary.kotlinprojectstudy.bean.MainColor
import com.mary.kotlinprojectstudy.util.EventUtil
import java.util.*
import kotlin.collections.HashMap

class MainColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view : View = itemView
    var viewColor : View = view.findViewById(R.id.viewColor)

    var id : Long = 0

    init {
        setListener()
    }

    fun updateView(item : MainColor) {

        viewColor.setBackgroundColor(Color.rgb(item.red,item.green,item.blue))
        id = item.id
    }

    private fun setListener(){
        viewColor.setOnClickListener {
            var hashMap : HashMap<String, Any> = hashMapOf()
            hashMap["id"]=id
            EventUtil.sendEvent("sendColor", this, hashMap)
        }
    }

}