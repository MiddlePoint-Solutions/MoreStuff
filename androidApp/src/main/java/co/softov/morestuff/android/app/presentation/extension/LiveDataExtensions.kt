package co.softov.morestuff.android.app.presentation.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> LifecycleOwner.observe(liveData: LiveData<T>, body: (T) -> Unit = {}) {
    liveData.observe(this, Observer { it?.let { t -> body(t) } })
}

fun <T> LifecycleOwner.observe(state: StateFlow<T>, body: (T) -> Unit = {}) {
    state.onEach {
        it?.let { t -> body(t) }
    }.launchIn(lifecycle.coroutineScope)
}