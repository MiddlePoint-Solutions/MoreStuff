package co.softov.morestuff.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import co.softov.morestuff.android.app.extensions.getParcelableExtraCompat
import co.softov.morestuff.android.app.navigation.AppRouter
import co.softov.morestuff.android.app.navigation.MoreStuffNavigator
import co.softov.morestuff.android.app.receiver.setReviewIntentExtras
import co.softov.morestuff.android.domain.enums.ReviewNotification
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.components.MoreStuffScaffold
import co.softov.morestuff.android.ui.main.MainContent
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private val navigatorHolder: NavigatorHolder by inject()
    private val appNavigator: Navigator by lazy {
        MoreStuffNavigator(this, android.R.id.content)
    }

    private val router: AppRouter by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showBatteryOptimizationRequest()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        onBackPressedDispatcher.addCallback(this) {
            router.exit()
        }

        handleLaunchIntent()

        setContent {
            TransparentSystemBars()
            val snackbarHostState = remember { SnackbarHostState() }

            MoreStuffTheme {
                MoreStuffScaffold(
                    snackbarHostState = snackbarHostState,
                    content = {
                        Box(Modifier.padding(top = it.calculateTopPadding())) {
                            MainContent(
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                )
            }

        }

    }

    @Composable
    private fun TransparentSystemBars() {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = !isSystemInDarkTheme()

        DisposableEffect(systemUiController, useDarkIcons) {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
            onDispose {}
        }
    }

    private fun handleLaunchIntent() {
        intent.getLongExtra(EXTRA_TASK_ID, 0).let {
            if (it > 0) {
                router.navigateTo(Screens.launchedTaskChat(it))
                intent.putExtra(EXTRA_TASK_ID, 0)
            }
        }
        intent.getParcelableExtraCompat(
            EXTRA_PRIORITY_REVIEW,
            ReviewNotification::class.java
        )?.let {
            router.navigateTo(Screens.review)
            intent = intent.setReviewIntentExtras(null)
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
        navigatorHolder.setNavigator(appNavigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    companion object {
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val EXTRA_PRIORITY_REVIEW = "EXTRA_PRIORITY_REVIEW"
    }
}