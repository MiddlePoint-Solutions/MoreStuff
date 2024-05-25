package io.middlepoint.morestuff.android.app.startup

import android.content.Context
import androidx.startup.Initializer
import io.middlepoint.morestuff.android.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}