package io.middlepoint.morestuff.shared

import android.app.Application
import androidx.work.Configuration
import io.middlepoint.morestuff.shared.platform.initializeSentry

class MoreStuffApp : Application(), Configuration.Provider {

  companion object {
    lateinit var INSTANCE: MoreStuffApp
  }

  override fun onCreate() {
    super.onCreate()
    INSTANCE = this
    initializeSentry()
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setMinimumLoggingLevel(android.util.Log.INFO)
      .build()
}
