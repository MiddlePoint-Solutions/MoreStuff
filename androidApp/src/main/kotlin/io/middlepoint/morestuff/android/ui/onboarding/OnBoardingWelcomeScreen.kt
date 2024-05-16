package io.middlepoint.morestuff.android.ui.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun OnBoardingWelcomeScreen(
    onNext: () -> Unit,
) {
    Box(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeContent)
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            contentDescription = stringResource(R.string.cd_app_logo),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.onboarding_welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.onboarding_welcome_subtitle1))
                    append("\n")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(stringResource(R.string.onboarding_welcome_subtitle2))
                    pop()
                    append("\n")
                    append(stringResource(R.string.onboarding_welcome_subtitle3))
                },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

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
    name = "Light",
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview() {
    MoreStuffTheme {
        OnBoardingWelcomeScreen {}
    }
}