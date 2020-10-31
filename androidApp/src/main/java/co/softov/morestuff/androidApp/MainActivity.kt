package co.softov.morestuff.androidApp

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.app.presentation.fragment.BaseFragment
import co.softov.morestuff.androidApp.app.presentation.fragment.FlowFragmentFactory
import co.softov.morestuff.androidApp.presentation.Screens
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val navigatorHolder: NavigatorHolder by inject()

    private val fragmentFactory: FlowFragmentFactory by inject()

    private val navigator: Navigator by lazy {
        object : AppNavigator(this, R.id.container, fragmentFactory = fragmentFactory) {

            override fun setupFragmentTransaction(
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment?
            ) {
                fragmentTransaction.setReorderingAllowed(true)
            }
        }
    }

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment

    private val router: Router by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_container)

        initViews()
        if (savedInstanceState == null) {
            router.newRootScreen(Screens.ContentFlow)
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    private fun initViews() {
        if (BuildConfig.DEBUG) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun showMainSettings() {
        router.navigateTo(Screens.Settings)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.iterator()?.forEach { it.isVisible = true }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        closeKeyboard()
        item?.let {
            when (it.itemId) {
                R.id.option_settings -> {
                    showMainSettings()
                }
            }
        }
        return false
    }

    private fun closeKeyboard() {
        // Check if no view has focus:
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
            v.clearFocus()
        }
    }
}