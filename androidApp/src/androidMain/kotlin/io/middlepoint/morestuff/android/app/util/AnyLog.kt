package io.middlepoint.morestuff.android.app.util

import android.util.Log
import timber.log.Timber

fun anyLog(any: Any, tag: String? = null) {
    Timber.d(tag?.let { "$it: %s" } ?: "%s", any.toString())
}