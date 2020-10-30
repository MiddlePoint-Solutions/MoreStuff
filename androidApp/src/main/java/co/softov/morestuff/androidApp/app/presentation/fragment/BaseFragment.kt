package co.softov.morestuff.androidApp.app.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
}
