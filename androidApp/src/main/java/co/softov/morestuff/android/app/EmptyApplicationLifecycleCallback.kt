package co.softov.morestuff.android.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

open class EmptyApplicationLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity) {
       
    }

    override fun onActivityStarted(activity: Activity) {
       
    }

    override fun onActivityDestroyed(activity: Activity) {
       
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
       
    }

    override fun onActivityStopped(activity: Activity) {
       
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
       
    }

    override fun onActivityResumed(activity: Activity) {
       
    }
}