package co.softov.morestuff.androidApp.app

import android.os.Build

fun isAtLeastVersion(versionCode: Int): Boolean {
    return Build.VERSION.SDK_INT >= versionCode
}
