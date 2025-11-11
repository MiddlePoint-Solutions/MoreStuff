package io.middlepoint.morestuff.shared

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import co.touchlab.kermit.Logger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REMINDER
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.ACTION_NOTIFICATION_REVIEW
import io.middlepoint.morestuff.shared.domain.navigation.Import
import io.middlepoint.morestuff.shared.domain.navigation.Review
import io.middlepoint.morestuff.shared.domain.navigation.AppRoute
import io.middlepoint.morestuff.shared.domain.navigation.TaskChat
import io.middlepoint.morestuff.shared.ui.App
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()

    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    FileKit.init(this)

    val launchScreen = handleLaunchIntent(intent)

    setContent {
      var initialAppRoute by remember { mutableStateOf<AppRoute?>(null) }
      App(initialAppRoute)

      LaunchedEffect(Unit) {
        if (initialAppRoute == null && launchScreen != null) {
          initialAppRoute = launchScreen
          Logger.d("Initial screen set from onNewIntent launchScreen: $launchScreen")
        }
      }

      DisposableEffect(Unit) {
        val listener = Consumer<Intent> {
          Logger.d("onNewIntent: $it")
          initialAppRoute = handleLaunchIntent(it)
          Logger.d("initialScreen: $initialAppRoute")
        }
        addOnNewIntentListener(listener)
        onDispose { removeOnNewIntentListener(listener) }
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
              Import.Text(it)
            }
          }

          // TODO: enable after supporting files
//          intent.type?.startsWith("image/") == true -> {
//            intent.getParcelableExtraCompat(Intent.EXTRA_STREAM, Uri::class.java)?.let {
//              Screen.Share(Shareable.Image(it.toString(), ""), it.toString())
//            }
//          }
//
//          "application/pdf" == intent.type -> {
//            intent.getParcelableExtraCompat(Intent.EXTRA_STREAM, Uri::class.java)?.let {
//              Screen.Share(Shareable.Pdf(it.toString(), ""), it.toString())
//            }
//          }

          else -> null
        }
      }

      Intent.ACTION_VIEW -> {
        val uri = intent.data
        Logger.d("DeepLink URI: $uri")
        if (uri?.scheme == "app.morestuff" && uri.host == "login-callback") {
          try {
            get<SupabaseClient>().handleDeeplinks(intent)
          } catch (e: IllegalArgumentException) {
            Logger.e("handleDeeplinks error", e)
          }
        }
        null
      }

      ACTION_NOTIFICATION_REMINDER -> {
        intent.getStringExtra(EXTRA_TASK_ID)?.let {
          TaskChat(it)
        }
      }

      ACTION_NOTIFICATION_REVIEW -> {
        intent.getStringExtra(EXTRA_SCOPE_ID)?.let {
          Review(it)
        }
      }

      else -> null
    }

  companion object {
    const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
    const val EXTRA_PRIORITY_REVIEW = "EXTRA_PRIORITY_REVIEW"
    const val EXTRA_SCOPE_ID = "EXTRA_SCOPE_ID"
  }
}
