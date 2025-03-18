package io.middlepoint.morestuff.shared.ui.screen.onboarding

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_next
import morestuff.composeapp.generated.resources.cd_app_logo
import morestuff.composeapp.generated.resources.logo
import morestuff.composeapp.generated.resources.onboarding_welcome_subtitle1
import morestuff.composeapp.generated.resources.onboarding_welcome_subtitle2
import morestuff.composeapp.generated.resources.onboarding_welcome_subtitle3
import morestuff.composeapp.generated.resources.onboarding_welcome_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun OnBoardingWelcomeScreen(
    onNext: () -> Unit,
) {
    Box(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeContent)
    ) {

        Image(
            painter = painterResource(Res.drawable.logo),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            contentDescription = stringResource(Res.string.cd_app_logo),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(Res.string.onboarding_welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = buildAnnotatedString {
                    append(stringResource(Res.string.onboarding_welcome_subtitle1))
                    append("\n")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(stringResource(Res.string.onboarding_welcome_subtitle2))
                    pop()
                    append("\n")
                    append(stringResource(Res.string.onboarding_welcome_subtitle3))
                },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        OnboardingButton(
            onClick = onNext,
            title = stringResource(Res.string.button_next),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light",
//    backgroundColor = 0xFFFFFFFF
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        OnBoardingWelcomeScreen {}
//    }
//}