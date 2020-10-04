package co.softov.morestuff.androidApp.app.presentation.extension

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Switch
import android.widget.TextView

fun Bundle.restoreViewState(vararg views: View) {
    views.forEach { it.loadInstanceState(this) }
}

fun Bundle.saveViewState(vararg views: View) {
    views.forEach { it.saveInstanceState(this) }
}

fun View.saveInstanceState(outState: Bundle) {
    when (this) {
        is TextView -> outState.putString(resources.getResourceName(id), text.toString())
        is Switch -> outState.putBoolean(resources.getResourceName(id), isChecked)
    }
}

fun View.loadInstanceState(savedInstanceState: Bundle) {
    when (this) {
        is TextView -> text = savedInstanceState.getString(resources.getResourceName(id))
        is Switch -> isChecked = savedInstanceState.getBoolean(resources.getResourceName(id))
    }
}

fun View.setOnDebouncedClickListener(action: () -> Unit) {
    val actionDebouncer =
        ActionDebouncer(action)

    // This is the only place in the project where we should actually use setOnClickListener
    setOnClickListener {
        actionDebouncer.notifyAction()
    }
}

fun View.removeOnDebouncedClickListener() {
    setOnClickListener(null)
    isClickable = false
}

private class ActionDebouncer(private val action: () -> Unit) {

    companion object {
        const val DEBOUNCE_INTERVAL_MILLISECONDS = 600L
    }

    private var lastActionTime = 0L

    fun notifyAction() {
        val now = SystemClock.elapsedRealtime()

        val millisecondsPassed = now - lastActionTime
        val actionAllowed = millisecondsPassed > DEBOUNCE_INTERVAL_MILLISECONDS
        lastActionTime = now

        if (actionAllowed) {
            action.invoke()
        }
    }
}
