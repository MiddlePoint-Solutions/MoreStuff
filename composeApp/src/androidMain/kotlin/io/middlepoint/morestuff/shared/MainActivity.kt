package io.middlepoint.morestuff.shared

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import co.touchlab.kermit.Logger
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.middlepoint.morestuff.shared.app.extensions.getParcelableExtraCompat
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.core.defaultScope
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.ui.App

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)
    enableEdgeToEdge()
    val rootRouterContext: RouterContext = defaultRouterContext()
    val launchScreen = handleLaunchIntent(intent)
    FileKit.init(this)

    setContent {
      CompositionLocalProvider(LocalRouterContext provides rootRouterContext) {

        var initialScreen by remember { mutableStateOf<Screen?>(null) }
        App(initialScreen)

        LaunchedEffect(Unit) {
          if (initialScreen == null && launchScreen != null) {
            initialScreen = launchScreen
            Logger.d("Initial screen set from onNewIntent launchScreen: $launchScreen")
          }
        }

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
