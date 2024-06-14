package io.middlepoint.morestuff.shared.app.extensions

import android.app.Dialog
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

fun Dialog.showKeyboard() {
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
}

fun Dialog.hideKeyboard() {
    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
}

fun Fragment.showKeyboard(view: View) {
    WindowInsetsControllerCompat(requireActivity().window, view).show(
        WindowInsetsCompat.Type.ime()
    )
}

fun Fragment.showKeyboard() {
    WindowInsetsControllerCompat(requireActivity().window, requireView()).show(
        WindowInsetsCompat.Type.ime()
    )
}

fun Fragment.hideKeyboard() {
    WindowInsetsControllerCompat(requireActivity().window, requireView()).hide(
        WindowInsetsCompat.Type.ime()
    )
}