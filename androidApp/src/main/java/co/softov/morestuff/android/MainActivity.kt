package co.softov.morestuff.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import co.softov.morestuff.android.app.extensions.getParcelableExtraCompat
import co.softov.morestuff.android.app.receiver.setReviewIntentExtras
import co.softov.morestuff.android.domain.enums.ReviewNotification
import co.softov.morestuff.android.nav.Screen
import co.softov.morestuff.android.nav.Screen.*
import co.softov.morestuff.android.ui.chat.task.TaskChatContent
import co.softov.morestuff.android.ui.home.HomeScreen
import co.softov.morestuff.android.ui.main.MainContent
import co.softov.morestuff.android.ui.main.ProvideComponentContext
import co.softov.morestuff.android.ui.review.ReviewContent
import co.softov.morestuff.android.ui.settings.SettingsScreen
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigationSource
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showBatteryOptimizationRequest()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        handleLaunchIntent()

        val rootComponentContext = defaultComponentContext()

        setContent {
            TransparentSystemBars()
            MoreStuffTheme {
                Surface {
                    ProvideComponentContext(rootComponentContext) {
                        MainContent()
                    }
                }
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
                // router.navigateTo(Screens.launchedTaskChat(it)) TODO: return
                intent.putExtra(EXTRA_TASK_ID, 0)
            }
        }
        intent.getParcelableExtraCompat(
            EXTRA_PRIORITY_REVIEW,
            ReviewNotification::class.java
        )?.let {
            // router.navigateTo(Screens.review) TODO:
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

    companion object {
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val EXTRA_PRIORITY_REVIEW = "EXTRA_PRIORITY_REVIEW"
    }
}