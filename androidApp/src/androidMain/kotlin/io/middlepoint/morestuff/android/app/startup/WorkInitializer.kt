package io.middlepoint.morestuff.android.app.startup

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager

class WorkInitializer : Initializer<Unit>, Configuration.Provider {

    override fun create(context: Context) {
        WorkManager.initialize(context, workManagerConfiguration)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

}