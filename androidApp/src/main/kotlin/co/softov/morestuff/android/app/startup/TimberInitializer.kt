package co.softov.morestuff.android.app.startup

import android.content.Context
import androidx.startup.Initializer
import co.softov.morestuff.android.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber Initialized")
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}