package io.middlepoint.morestuff.shared.ui.screen.onboarding

import KottieAnimation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import kottieComposition.KottieCompositionSpec
import kottieComposition.animateKottieCompositionAsState
import kottieComposition.rememberKottieComposition
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_next
import morestuff.composeapp.generated.resources.onboarding_chat_task_subtitle
import morestuff.composeapp.generated.resources.onboarding_chat_task_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalResourceApi::class)
@Composable
fun OnBoardingTaskChatScreen(onNext: () -> Unit) {

    var animation by remember { mutableStateOf("") }

    LaunchedEffect(Unit){
        animation = Res.readBytes("files/animation_chat.json").decodeToString()
    }

    val composition = rememberKottieComposition(
        spec = KottieCompositionSpec.File(animation) // Or KottieCompositionSpec.Url || KottieCompositionSpec.JsonString
    )

    val animationState by animateKottieCompositionAsState(
        composition = composition,
        isPlaying = true
    )

    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.safeContent)
            .fillMaxSize()
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {

            val (image, title, subtitle) = createRefs()

            Text(
                text = stringResource(Res.string.onboarding_chat_task_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) { top.linkTo(parent.top, margin = 40.dp) },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp,
                    lineHeight = 44.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = stringResource(Res.string.onboarding_chat_task_subtitle),
                modifier = Modifier
                    .constrainAs(subtitle) { top.linkTo(title.bottom, margin = 20.dp) }
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary,
            )

            KottieAnimation(
                composition = animationState.composition,
                modifier = Modifier
                    .constrainAs(image) { centerTo(parent) }
                    .size(300.dp),
                progress = { animationState.progress }
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


@Preview
@Composable
private fun Preview() {
    MoreStuffTheme {
        OnBoardingTaskChatScreen {}
    }
}