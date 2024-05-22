package io.middlepoint.morestuff.android.app.startup

import android.content.Context
import androidx.startup.Initializer
import io.middlepoint.morestuff.shared.di.dataModule
import io.middlepoint.morestuff.shared.di.domainModules
import io.middlepoint.morestuff.shared.di.featuresModule
import io.middlepoint.morestuff.shared.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class KoinInitializer: Initializer<KoinApplication> {

    override fun create(context: Context): KoinApplication {
        return startKoin {
            androidLogger()
            androidContext(context)
            modules(
                modules = buildList {
                    addAll(domainModules)
                    add(dataModule)
                    add(presentationModule)
                    add(featuresModule)
                }
            )
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(FirebaseInitializer::class.java, TimberInitializer::class.java)
    }

}