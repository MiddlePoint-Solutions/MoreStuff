package co.softov.morestuff.androidApp.app.presentation.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import co.softov.morestuff.androidApp.presentation.content.ContentFlowFragment
import com.github.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject

class FlowFragmentFactory @Inject constructor(
    private val navigatorHolder: NavigatorHolder
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ContentFlowFragment::javaClass.name -> ContentFlowFragment(navigatorHolder)
            else -> super.instantiate(classLoader, className)
        }
    }
}