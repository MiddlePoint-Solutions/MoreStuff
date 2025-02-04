package io.middlepoint.morestuff.shared

import android.app.Application

class MoreStuffApp : Application() {
  companion object {
    lateinit var INSTANCE: MoreStuffApp
  }

  override fun onCreate() {
    super.onCreate()
    INSTANCE = this
  }
}
