package io.middlepoint.morestuff.shared.app.extensions

import android.content.Intent
import android.os.Build

fun <T> Intent.getParcelableExtraCompat(name: String?, clazz: Class<T>) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, clazz)
    } else {
        getParcelableExtra(name)
    }