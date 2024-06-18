package io.middlepoint.morestuff.shared

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.router.stack.navigate
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.middlepoint.morestuff.shared.app.extensions.getParcelableExtraCompat
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import io.middlepoint.morestuff.shared.ui.local.ProvideAppRouter
import io.middlepoint.morestuff.shared.ui.local.ProvideAppTheme
import io.middlepoint.morestuff.shared.ui.screen.main.MainContent
import io.middlepoint.morestuff.shared.ui.screen.main.MainEvent
import io.middlepoint.morestuff.shared.ui.screen.main.MainViewModel
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.ui.App
import org.koin.android.ext.android.inject
import org.koin.compose.KoinContext

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    val rootRouterContext: RouterContext = defaultRouterContext()

    setContent {
      CompositionLocalProvider(LocalRouterContext provides rootRouterContext) {

        var initialScreen by remember { mutableStateOf<Screen?>(null) }
        App(initialScreen)

        DisposableEffect(Unit) {
          val listener = Consumer<Intent> {
            initialScreen = handleLaunchIntent(it)
            Logger.d("onNewIntent: $initialScreen")
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
        Logger.d("intent.type: ${intent.type}")
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
