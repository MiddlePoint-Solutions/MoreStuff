package co.softov.morestuff.android.app.presentation.extension

import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

inline fun Fragment.launchWithDelay(timeMillis: Long, crossinline action: () -> Unit) {
    lifecycleScope.launch {
        delay(timeMillis)
        action.invoke()
    }
}

fun Fragment.longRes(@IntegerRes id: Int) = resources.getInteger(id).toLong()
