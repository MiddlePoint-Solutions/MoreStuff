package co.softov.morestuff.android.ui.onboarding

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.delay

@Composable
fun OnBoardingReviewScreen(
    nextButtonText: String = stringResource(id = R.string.button_next),
    onNext: () -> Unit,
) {

    val context = LocalContext.current
    val tasks = remember { buildReviewHintTasks(context) }
    var showExample by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        showExample = true
    }

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeContent)
            .fillMaxSize(),
    ) {

        Text(
            text = stringResource(R.string.onboarding_review_title),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 40.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.primary,
        )

        AnimatedVisibility(
            visible = showExample,
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(1f),
            enter = fadeIn()
        ) {
            OnBoardingReviewCards(
                tasks = tasks,
            )
        }

        OnboardingButton(
            onClick = onNext,
            title = nextButtonText,
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
        OnBoardingReviewScreen("Next") {}
    }
}
