package io.middlepoint.morestuff.android.app.startup

import MoreStuff.androidApp.BuildConfig
import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}