package co.softov.morestuff.android.ui.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import arrow.core.const
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import kotlinx.coroutines.delay

@Composable
fun OnBoardingReviewScreen(
    onNext: () -> Unit,
) {

    val context = LocalContext.current
    val tasks = remember { buildReviewHintTasks(context) }
    var reloadCards by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeContent)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = stringResource(R.string.onboarding_review_title),
                modifier = Modifier.padding(top = 40.dp),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.primary,
            )
        }

        OnBoardingReviewCards(
            tasks = tasks,
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(1f)
        )

        OnboardingButton(
            onClick = onNext,
            title = stringResource(id = R.string.button_next),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )

    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
private fun Preview() {
    MoreStuffTheme {
        OnBoardingReviewScreen {}
    }
}