package io.middlepoint.morestuff.android

import android.app.Application
import io.middlepoint.morestuff.android.app.service.NotifierImpl
import io.middlepoint.morestuff.android.app.service.SchedulerImpl
import io.middlepoint.morestuff.android.di.presentationModule
import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MoreStuff : Application() {

  override fun onCreate() {
    super.onCreate()
    initKoin {
      androidLogger()
      androidContext(this@MoreStuff)

      module {
        singleOf(::SchedulerImpl) bind Scheduler::class
        singleOf(::NotifierImpl) bind Notifier::class
      }

      modules(presentationModule)

    }
  }

}