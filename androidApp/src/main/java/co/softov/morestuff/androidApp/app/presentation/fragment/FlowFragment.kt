package co.softov.morestuff.androidApp.app.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.presentation.extension.setLaunchScreen
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.AppScreen
import org.koin.android.ext.android.inject

abstract class FlowFragment(
    private val navigatorHolder: NavigatorHolder
) : BaseFragment() {

    override val layoutResourceId: Int = R.layout.layout_container

    open val mainContainerId: Int = R.id.container

    private val currentFragment
        get() = childFragmentManager.findFragmentById(mainContainerId) as? BaseFragment

    private val navigator: Navigator by lazy {
        object : AppNavigator(requireActivity(), mainContainerId, childFragmentManager) {

            override fun setupFragmentTransaction(
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment?
            ) {
                fragmentTransaction.setReorderingAllowed(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (childFragmentManager.fragments.isEmpty()) {
            navigator.setLaunchScreen(getLaunchScreen())
        }
    }

    abstract fun getLaunchScreen(): AppScreen

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}