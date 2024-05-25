package io.middlepoint.morestuff.android.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.navigate
import io.middlepoint.morestuff.android.app.extensions.getParcelableExtraCompat
import io.middlepoint.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import io.middlepoint.morestuff.android.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import io.middlepoint.morestuff.android.ui.local.ProvideAppNavigation
import io.middlepoint.morestuff.android.ui.local.ProvideAppTheme
import io.middlepoint.morestuff.android.ui.main.MainContent
import io.middlepoint.morestuff.android.ui.main.MainEvent
import io.middlepoint.morestuff.android.ui.main.MainViewModel
import io.middlepoint.morestuff.android.ui.navigation.ProvideComponentContext
import io.middlepoint.morestuff.android.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.domain.nav.Screen
import org.koin.android.ext.android.inject
import org.koin.compose.KoinContext
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val rootComponentContext = defaultComponentContext()

        val viewModel: MainViewModel by inject<MainViewModel>()

        setContent {
            KoinContext {

                val model by viewModel.models.collectAsState()

                ProvideComponentContext(rootComponentContext) {

                    val navigation = remember { StackNavigation<Screen>() }

                    ProvideAppTheme(model.theme) {

                        MoreStuffTheme {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer
                            ) {
                                ProvideAppNavigation(navigation) {
                                    if (model.ready) {
                                        val initialScreen = if (model.showOnBoarding) {
                                            Screen.OnBoarding
                                        } else {
                                            handleLaunchIntent(intent)
                                        }

                                        MainContent(
                                            initialScreen = initialScreen,
                                            shareContent = { taskId, content ->
                                                viewModel.take(
                                                    MainEvent.ShareContent(taskId, content)
                                                )
                                            },
                                            onBoardingComplete = {
                                                viewModel.take(MainEvent.OnBoardingComplete)
                                            }
                                        )
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
