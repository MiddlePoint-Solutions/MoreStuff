package co.softov.morestuff.android.app.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment : Fragment() {

    @get:LayoutRes
    protected abstract val layoutResourceId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(layoutResourceId, container, false).also {
            Timber.v("onCreateView ${javaClass.simpleName}")
        }

    open fun onBackPressed() {}

    protected fun closeKeyboard() {
        requireView().let { v ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
            v.clearFocus()
        }
    }
}
