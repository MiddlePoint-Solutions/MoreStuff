package co.softov.morestuff.android.app.startup

import android.content.Context
import androidx.startup.Initializer
import co.softov.morestuff.android.di.dataModule
import co.softov.morestuff.android.di.domainModules
import co.softov.morestuff.android.di.featuresModule
import co.softov.morestuff.android.di.presentationModule
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