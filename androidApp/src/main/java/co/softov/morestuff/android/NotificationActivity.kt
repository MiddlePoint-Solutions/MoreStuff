package co.softov.morestuff.android

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import co.softov.morestuff.android.app.navigation.AppRouter
import co.softov.morestuff.android.app.presentation.compose.modifier.runOnTap
import co.softov.morestuff.android.ui.Screens
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import org.koin.android.ext.android.inject

//TODO: Launch priority review, should be used to show over other apps after getting permission SYSTEM_ALERT
class NotificationActivity : AppCompatActivity() {

    private val router: AppRouter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        setContent {
            MoreStuffTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .runOnTap {
                            finish()
                        },
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Blue)
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .runOnTap {}
                    ) {
                        PriorityButton(
                            onSelected = {
                                router.navigateTo(Screens.priorityReview)
                                finish()
                            },
                            text = stringResource(R.string.start).uppercase(),
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .padding(16.dp)
                                .align(Alignment.Center),
                            fontSize = 26.sp,
                            shape = RoundedCornerShape(50)
                        )
                    }

                }
            }
        }
    }
}


