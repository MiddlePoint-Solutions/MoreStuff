package co.softov.morestuff.android.app.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LifecycleValue<T : Any> : ReadWriteProperty<LifecycleOwner, T>, LifecycleObserver {
    private var _value: T? = null

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T =
        _value ?: throw IllegalStateException("Trying to call an auto-cleared value outside of the view lifecycle.")

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        thisRef.lifecycle.removeObserver(this)
        _value = value
        thisRef.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        _value = null
    }
}