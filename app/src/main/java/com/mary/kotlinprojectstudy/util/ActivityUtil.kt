package com.mary.kotlinprojectstudy.util

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

object ActivityUtil {
    fun startActivityWithoutFinish(context: Context, cls: Class<*>?) {
        val intent = Intent(context, cls)
        context.startActivity(intent)
    }

    fun startActivityWithoutFinish(context: Context, cls: Class<*>?, bundle: Bundle) {
        val intent = Intent(context, cls)
        intent.putExtra("BUNDLE_KEY", bundle)
        context.startActivity(intent)
    }

    fun startActivityWithFinish(context: Context, cls: Class<*>?) {
        val intent = Intent(context, cls)
        context.startActivity(intent)
        val activity = context as Activity
        activity.finish()
    }
}