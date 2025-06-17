package io.middlepoint.morestuff.shared.startup

import android.content.Context
import androidx.startup.Initializer
import io.middlepoint.morestuff.shared.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication

class KoinInitializer : Initializer<KoinApplication> {

  override fun create(context: Context): KoinApplication {
    return initKoin {
      androidLogger()
      androidContext(context)
    }
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(WorkInitializer::class.java)
  }

}