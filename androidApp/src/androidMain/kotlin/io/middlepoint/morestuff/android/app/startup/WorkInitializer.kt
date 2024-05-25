package io.middlepoint.morestuff.android.app.startup

import android.content.Context
import androidx.startup.Initializer
import androidx.work.WorkManager
import io.middlepoint.morestuff.android.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

class WorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        WorkManager.getInstance(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}