package com.mary.kotlinprojectstudy.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mary.kotlinprojectstudy.R

class NavigationViewHolder @JvmOverloads constructor(
    context: Context
){

    interface NavigationViewHolderDelegate {
        fun back()
    }

    lateinit var imageViewBack: ImageView
    lateinit var textViewTitle : TextView
    var navigationViewHolderDelegate : NavigationViewHolderDelegate? = null

    var view: View = LayoutInflater.from(context).inflate(R.layout.view_holder_navigation, null)

    init {
        findView()
        setListener()
    }


    private fun findView() {
        imageViewBack = view.findViewById(R.id.imageViewBack)
        textViewTitle = view.findViewById(R.id.textViewTitle)
    }

    private fun setListener() {
        imageViewBack.setOnClickListener {
            navigationViewHolderDelegate!!.back()
        }
    }

    fun setTitle(title : String) {
        textViewTitle.text = title
    }

}