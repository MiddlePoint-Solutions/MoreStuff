package co.softov.morestuff.android.app.util

import androidx.lifecycle.*

/**
 * Temp fix for Koin's inability to create multiple instances of the same ViewModel with
 * different parameters.
 */
class LifecycleViewModelStoreOwner(lifecycleOwner: LifecycleOwner?) : ViewModelStoreOwner,
    DefaultLifecycleObserver {

     override val viewModelStore = ViewModelStore()

    init {
        if (lifecycleOwner == null) {
            throw IllegalStateException("Lifecycle owner must not be null")
        } else {
            lifecycleOwner.lifecycle.addObserver(this)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        viewModelStore.clear()
    }
}