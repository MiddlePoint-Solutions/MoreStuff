package co.softov.morestuff.android.app.util

import android.util.Log

fun anyLog(any: Any, tag: String? = null) {
    Log.d(tag ?: "ANY_LOG", any.toString())
}