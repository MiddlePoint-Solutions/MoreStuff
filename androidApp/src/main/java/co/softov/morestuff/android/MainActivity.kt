package co.softov.morestuff.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.ListFragment
import co.softov.morestuff.android.app.presentation.fragment.BaseFragment
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.components.MoreStuffScaffold
import co.softov.morestuff.android.ui.list.ListsFragment
import co.softov.morestuff.android.ui.main.MainConductor
import co.softov.morestuff.android.ui.main.MainContent
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val navigatorHolder: NavigatorHolder by inject()
    private val router: Router by inject()

    private val navigator: Navigator = object : AppNavigator(this, android.R.id.content) {

        override fun setupFragmentTransaction(
            screen: FragmentScreen,
            fragmentTransaction: FragmentTransaction,
            currentFragment: Fragment?,
            nextFragment: Fragment,
        ) {
            if (screen == Screens.TaskChat) {
                fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right
                )
            }
        }
    }


    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment

    private val conductor = object : MainConductor {

        override fun showTaskList() {
            ListsFragment().show(supportFragmentManager, ListFragment::javaClass.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showBatteryOptimizationRequest()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MoreStuffTheme {
                MoreStuffScaffold(
                    content = {
                        MainContent(conductor)
                    },
                    scope = rememberCoroutineScope(),
                    onItemClicked = {
                        showMainSettings()
                    },
                )
            }
        }
    }

    @SuppressLint("BatteryLife")
    private fun showBatteryOptimizationRequest() {
        val powerManager = getSystemService(PowerManager::class.java)
        val ignoringOptimization = powerManager.isIgnoringBatteryOptimizations(packageName)
        Timber.d("Ignoring Battery optimizations: $ignoringOptimization")

        //TODO: request that the user adds MoreStuff to the whitelist.
        if (!ignoringOptimization) {
            val intent = Intent()
            intent.action = ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
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

    private fun showMainSettings() {
        router.navigateTo(Screens.Settings)
    }
}