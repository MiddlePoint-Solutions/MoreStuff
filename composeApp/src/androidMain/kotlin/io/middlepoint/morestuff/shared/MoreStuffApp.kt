package io.middlepoint.morestuff.shared

import android.app.Application
import androidx.work.Configuration

class MoreStuffApp : Application(), Configuration.Provider {

  companion object {
    lateinit var INSTANCE: MoreStuffApp
  }

  override fun onCreate() {
    super.onCreate()
    INSTANCE = this
  }

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setMinimumLoggingLevel(android.util.Log.INFO)
      .build()
}
