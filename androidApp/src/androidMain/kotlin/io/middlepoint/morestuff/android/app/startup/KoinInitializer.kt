package io.middlepoint.morestuff.android.app.startup

import android.content.Context
import androidx.startup.Initializer
import io.middlepoint.morestuff.android.app.service.NotifierImpl
import io.middlepoint.morestuff.android.app.service.SchedulerImpl
import io.middlepoint.morestuff.android.di.presentationModule
import io.middlepoint.morestuff.shared.di.dataModule
import io.middlepoint.morestuff.shared.di.domainModules
import io.middlepoint.morestuff.shared.di.initKoin
import io.middlepoint.morestuff.shared.domain.service.Notifier
import io.middlepoint.morestuff.shared.domain.service.Scheduler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class KoinInitializer: Initializer<KoinApplication> {

    override fun create(context: Context): KoinApplication {
        return initKoin {
            androidLogger()
            androidContext(context)
            modules(presentationModule)
        }


    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(FirebaseInitializer::class.java)
    }

}