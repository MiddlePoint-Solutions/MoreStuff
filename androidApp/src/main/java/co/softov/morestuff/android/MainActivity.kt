package co.softov.morestuff.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import co.softov.morestuff.android.app.presentation.fragment.BaseFragment
import co.softov.morestuff.android.ui.Screens
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val navigatorHolder: NavigatorHolder by inject()
    private val router: Router by inject()

    private val navigator: Navigator by lazy {
        AppNavigator(this, R.id.main_content)
    }

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showBatteryOptimizationRequest()

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)
        router.newRootScreen(Screens.Content)
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
}