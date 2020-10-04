package co.softov.morestuff.androidApp.app.service

import android.app.IntentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

abstract class BaseService(name: String) : IntentService(name) {

    private val serviceJob = Job()
    protected val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

}