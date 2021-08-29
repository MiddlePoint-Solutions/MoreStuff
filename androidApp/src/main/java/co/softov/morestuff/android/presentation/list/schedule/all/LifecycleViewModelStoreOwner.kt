package co.softov.morestuff.android.presentation.list.schedule.all

import androidx.lifecycle.*

/**
 * Temp fix for Koin's inability to create multiple instances of the same ViewModel with
 * different parameters.
 */
class LifecycleViewModelStoreOwner(lifecycleOwner: LifecycleOwner?) : ViewModelStoreOwner,
    LifecycleObserver {

    private val viewModelStore = ViewModelStore()

    init {
        if (lifecycleOwner == null) {
            throw IllegalStateException("Lifecycle owner must not be null")
        } else {
            lifecycleOwner.lifecycle.addObserver(this)
        }
    }

    override fun getViewModelStore(): ViewModelStore = viewModelStore

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun clear() {
        viewModelStore.clear()
    }

}