package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.finish
import morestuff.shared.generated.resources.onboarding_complete_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun OnBoardingCompleteScreen(onFinish: () -> Unit) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeContent)
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(Res.string.onboarding_complete_title),
            modifier = Modifier
                .padding(top = 40.dp)
                .align(Alignment.TopCenter),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 40.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            ),
            color = MaterialTheme.colorScheme.primary,
        )

//        Image(
//            painter = painterResource(Res.drawable.onboarding_workspace),
//            modifier = Modifier.align(Alignment.Center),
//            contentDescription = "Workspace desk image",
//            contentScale = ContentScale.FillBounds
//        )

        OnboardingButton(
            onClick = onFinish,
            title = stringResource(Res.string.finish),
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
//    name = "Light"
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        OnBoardingCompleteScreen {}
//    }
//}
