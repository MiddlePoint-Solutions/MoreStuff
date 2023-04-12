package co.softov.morestuff.android

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.core.view.WindowCompat
import co.softov.morestuff.android.app.navigation.AppRouter
import co.softov.morestuff.android.ui.components.MoreStuffScaffold
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import org.koin.android.ext.android.inject


class NotificationActivity : AppCompatActivity() {

    private val router: AppRouter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        onBackPressedDispatcher.addCallback(this) {
            router.exit()
        }

        setContent {
            MoreStuffTheme {
                Surface {
                    Text(text = "HEllo world")
                }
            }
        }
    }
}