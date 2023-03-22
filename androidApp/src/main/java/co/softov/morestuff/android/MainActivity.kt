package co.softov.morestuff.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.ListFragment
import co.softov.morestuff.android.ui.ScreenKey
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
            nextFragment: Fragment
        ) {
            when {
                screen.screenKey.contains(ScreenKey.LAUNCHED_TASK_CHAT) -> {
                    fragmentTransaction.setCustomAnimations(
                        0,
                        R.anim.slide_out_right,
                        0,
                        R.anim.slide_out_right
                    )
                }
                screen.screenKey.contains(ScreenKey.TASK_CHAT) -> {
                    fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_right,
                        R.anim.slide_in_right,
                        R.anim.slide_out_right
                    )
                }
            }
        }
    }

    private val conductor = object : MainConductor {
        override fun showTaskList() {
            ListsFragment().show(supportFragmentManager, ListFragment::javaClass.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showBatteryOptimizationRequest()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        onBackPressedDispatcher.addCallback(this) {
            router.exit()
        }

        intent.getLongExtra(EXTRA_TASK_ID, 0).let {
            if (it > 0) {
                router.navigateTo(Screens.launchedTaskChat(it))
                intent.putExtra(EXTRA_TASK_ID, 0)
            }
        }

        setContent {
            MoreStuffTheme {
                MoreStuffScaffold(
                    content = {
                        MainContent(conductor)
                    },
                    onSettingsClicked = {
                        showSettingsScreen()
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

    private fun showSettingsScreen() {
        router.navigateTo((Screens.ComposeSettings))
    }


    companion object {

        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"

    }
}