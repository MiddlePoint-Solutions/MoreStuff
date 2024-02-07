package co.softov.morestuff.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import co.softov.morestuff.android.app.extensions.getParcelableExtraCompat
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import co.softov.morestuff.android.app.util.LifecycleEventsObserver
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.domain.nav.Shareable
import co.softov.morestuff.android.ui.local.ProvideAppNavigation
import co.softov.morestuff.android.ui.local.ProvideAppTheme
import co.softov.morestuff.android.ui.main.MainContent
import co.softov.morestuff.android.ui.main.MainStates
import co.softov.morestuff.android.ui.main.MainViewModel
import co.softov.morestuff.android.ui.navigation.ProvideComponentContext
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.navigate
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        showBatteryOptimizationRequest()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootComponentContext = defaultComponentContext()

        val viewModel: MainViewModel by inject<MainViewModel>()

        setContent {

            val stateModel by viewModel.states.collectAsState()

            ProvideComponentContext(rootComponentContext) {


                val navigation = remember { StackNavigation<Screen>() }

                ProvideAppTheme(viewModel.appTheme) {
                    MoreStuffTheme {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            ProvideAppNavigation(navigation) {
                                when (val model = stateModel) {
                                    MainStates.Loading -> {}
                                    is MainStates.Ready -> {
                                        val initialScreen = if (model.showOnBoarding) {
                                            Screen.OnBoarding
                                        } else {
                                            handleLaunchIntent(intent)
                                        }

                                        KoinAndroidContext {
                                            MainContent(
                                                initialScreen = initialScreen,
                                                shareContent = viewModel::shareContentToTask
                                            )
                                        }
                                    }
                                }
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
    }

    private fun handleLaunchIntent(intent: Intent) =
        when (intent.action) {
            Intent.ACTION_SEND -> {
                Timber.d("intent.type: ${intent.type}")
                when {
                    "text/plain" == intent.type -> {
                        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                            Screen.Share(Shareable.Text(it), it)
                        }
                    }

                    intent.type?.startsWith("image/") == true -> {
                        intent.getParcelableExtraCompat(Intent.EXTRA_STREAM, Uri::class.java)?.let {
                            Screen.Share(Shareable.Image(it.toString(), ""), it.toString())
                        }
                    }

                    "application/pdf" == intent.type -> {
                        intent.getParcelableExtraCompat(Intent.EXTRA_STREAM, Uri::class.java)?.let {
                            Screen.Share(Shareable.Pdf(it.toString(), ""), it.toString())
                        }
                    }

                    else -> null
                }
            }

            ACTION_NOTIFICATION_REMINDER -> {
                Screen.TaskChat(intent.getLongExtra(EXTRA_TASK_ID, 0))
            }

            ACTION_NOTIFICATION_REVIEW -> {
                Screen.Review(intent.getLongExtra(EXTRA_SCOPE_ID, defaultScope.id))
            }

            else -> null
        }

    companion object {
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val EXTRA_PRIORITY_REVIEW = "EXTRA_PRIORITY_REVIEW"
        const val EXTRA_SCOPE_ID = "EXTRA_SCOPE_ID"
    }
}
