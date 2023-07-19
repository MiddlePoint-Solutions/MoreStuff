package co.softov.morestuff.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.domain.nav.Shareable
import co.softov.morestuff.android.ui.main.MainContent
import co.softov.morestuff.android.ui.navigation.ProvideComponentContext
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.navigate
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showBatteryOptimizationRequest()
        WindowCompat.setDecorFitsSystemWindows(window, false)


        val rootComponentContext = defaultComponentContext()

        setContent {

            val viewModel: MainViewModel = koinViewModel()
            val navigation = remember { StackNavigation<Screen>() }

            LifecycleEventsObserver(
                onResume = { viewModel.onResume() }
            )

            val model by viewModel.model.collectAsState()
            val initialScreen = if (model.showOnBoarding) {
                Screen.OnBoarding
            } else {
                handleLaunchIntent(intent)
            }

            TransparentSystemBars()

            CompositionLocalProvider(LocalTheme provides viewModel.appTheme) {
                MoreStuffTheme {
                    Surface {
                        ProvideComponentContext(rootComponentContext) {
                            MainContent(
                                initialScreen = initialScreen,
                                navigation = navigation,
                                shareContent = viewModel::shareTextToTask
                            )
                        }
                    }
                }
            }

            DisposableEffect(Unit) {
                val listener = Consumer<Intent> {
                    val screen = handleLaunchIntent(it)
                    Timber.d("onNewIntent: $screen")
                    if (screen != null) {
                        navigation.navigate {
                            listOf(Screen.Home, screen)
                        }
                    }
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
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

    private fun handleLaunchIntent(intent: Intent): Screen? = when (intent.action) {
        Intent.ACTION_SEND -> {
            if ("text/plain" == intent.type) {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    Screen.Share(Shareable.Text, it)
                }
            } else if (intent.type?.startsWith("image/") == true) {
                TODO("Implement image sharing")
            } else null
        }

        ACTION_NOTIFICATION_REMINDER -> {
            Screen.TaskChat(intent.getLongExtra(EXTRA_TASK_ID, 0))
        }

        ACTION_NOTIFICATION_REVIEW -> {
            Screen.Review
        }

        else -> null
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

val LocalTheme = compositionLocalOf { AppTheme.System }