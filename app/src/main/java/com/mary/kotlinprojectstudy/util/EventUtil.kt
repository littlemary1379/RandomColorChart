package com.mary.kotlinprojectstudy.util

import java.lang.ref.WeakReference

object EventUtil {

    private const val TAG = "EventUtil"
    private var hashMap = HashMap<String, ArrayList<EventObserver>>()

    interface EventRunnable {
        fun run(arrow: String, poster: Any, data: HashMap<String, Any>?)
    }

    class EventObserver {
        var arrow : String
        var objectWeakReference : WeakReference<Any>
        var runnable: EventRunnable

        //보조생성자
        constructor(arrow: String, obj : Any, runnable: EventRunnable) {
            this.arrow = arrow
            this.objectWeakReference = WeakReference(obj)
            this.runnable = runnable
        }

    }

    fun addEventObserver(arrow: String, obj: Any, runnable: EventRunnable) {
        DlogUtil.d(TAG, "여긴???????????? $arrow")
        var eventObserver = EventObserver(arrow, obj, runnable)
        getObserverListForArrow(arrow)?.add(eventObserver)

    }

    fun sendEvent(arrow: String, sender: Any, data: HashMap<String, Any>?) {
        var result : ArrayList<EventObserver>? = getObserverListForArrow(arrow)
        DlogUtil.d(TAG, "이거 되긴 해???????????? $result")
        result?.forEach {
            it.runnable.run(arrow, sender, data)
        }
    }

    private fun getObserverListForArrow(arrow: String) : ArrayList<EventObserver>? {
        var result : ArrayList<EventObserver>? = hashMap[arrow]

        if (result==null) {
            result = ArrayList()
            hashMap[arrow] = result
        }

        return result

    }

}