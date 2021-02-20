package co.softov.morestuff.android.app.extensions

import android.os.Build

fun isAtLeastVersion(versionCode: Int): Boolean {
    return Build.VERSION.SDK_INT >= versionCode
}
