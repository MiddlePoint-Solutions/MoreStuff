package co.softov.morestuff.android.app.util

import androidx.lifecycle.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LifecycleValue<T : Any> : ReadWriteProperty<LifecycleOwner, T>, DefaultLifecycleObserver {
    private var _value: T? = null

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T =
        _value
            ?: throw IllegalStateException("Trying to call an auto-cleared value outside of the view lifecycle.")

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        thisRef.lifecycle.removeObserver(this)
        _value = value
        thisRef.lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        _value = null
    }
}