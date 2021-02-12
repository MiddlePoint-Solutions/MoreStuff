package co.softov.morestuff.android

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.preference.PreferenceManager
import co.softov.morestuff.android.app.EmptyApplicationLifecycleCallback
import co.softov.morestuff.android.di.*
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class MsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setNightMode()
        registerActivityLifecycleCallbacks(ApplicationLifecycle())
        initTimber()
        initKoin()
        initFirebase()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@MsApplication)
            modules(
                listOf(
                    serviceModule,
                    storeModule,
                    taskUseCases,
                    messageUseCases,
                    scheduleUseCases,
                    dataModule,
                    presentationModule,
                    navigationModule
                )
            )
        }
    }

    private fun setNightMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themeKey = getString(R.string.pref_key_app_theme)
        prefs.getString(themeKey, "-1")?.let {
            setDefaultNightMode(it.toInt())
        }
    }

    private fun initFirebase() {
        // Enable Firebase analytics in release build only
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(BuildConfig.DEBUG)
    }

    companion object {
        var isForeground: Boolean = false
    }

    internal class ApplicationLifecycle : EmptyApplicationLifecycleCallback() {

        override fun onActivityResumed(activity: Activity) {
            isForeground = true
            Timber.d("onActivityResumed: ${activity.javaClass.simpleName}")
        }

        override fun onActivityPaused(activity: Activity) {
            isForeground = false
            Timber.d("onActivityPaused: ${activity.javaClass.simpleName}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            isForeground = false
            Timber.d("onActivityDestroyed: ${activity.javaClass.simpleName}")
        }

    }

}