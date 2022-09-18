package co.softov.morestuff.android.app.service

import android.app.IntentService
import co.softov.morestuff.android.domain.redux.store.Action
import co.softov.morestuff.android.domain.redux.AppStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject

abstract class BaseService(name: String) : IntentService(name) {

    private val store: AppStore by inject()

    protected fun dispatchStoreAction(action: Action) {
        store.dispatch(action)
    }

    private val serviceJob = Job()
    protected val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

}