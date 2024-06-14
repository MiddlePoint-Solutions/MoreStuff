//package io.middlepoint.morestuff.shared.ui.screen.onboarding
//
//import android.content.res.Configuration
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.safeContent
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.windowInsetsPadding
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ConstraintLayout
//import io.middlepoint.morestuff.android.R
//import io.middlepoint.morestuff.android.ui.theme.MoreStuffTheme
//import com.airbnb.lottie.compose.LottieAnimation
//import com.airbnb.lottie.compose.LottieCompositionSpec
//import com.airbnb.lottie.compose.LottieConstants
//import com.airbnb.lottie.compose.rememberLottieAnimatable
//import com.airbnb.lottie.compose.rememberLottieComposition
//import kotlinx.coroutines.delay
//
//@Composable
//fun OnBoardingTaskChatScreen(onNext: () -> Unit) {
//
//    val composition by rememberLottieComposition(
//        LottieCompositionSpec.RawRes(R.raw.animation_lncy8nut)
//    )
//
//    Box(
//        modifier = Modifier
//            .windowInsetsPadding(WindowInsets.safeContent)
//            .fillMaxSize()
//    ) {
//        ConstraintLayout(
//            modifier = Modifier.fillMaxSize(),
//        ) {
//
//            val (image, title, subtitle) = createRefs()
//
//            Text(
//                text = stringResource(R.string.onboarding_chat_task_title),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .constrainAs(title) { top.linkTo(parent.top, margin = 40.dp) },
//                style = MaterialTheme.typography.headlineMedium.copy(
//                    fontSize = 40.sp,
//                    lineHeight = 44.sp,
//                    fontWeight = FontWeight.Black,
//                    textAlign = TextAlign.Center
//                ),
//                color = MaterialTheme.colorScheme.primary,
//            )
//
//            Text(
//                text = stringResource(R.string.onboarding_chat_task_subtitle),
//                modifier = Modifier
//                    .constrainAs(subtitle) { top.linkTo(title.bottom, margin = 20.dp) }
//                    .padding(horizontal = 20.dp),
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.headlineSmall,
//                color = MaterialTheme.colorScheme.secondary,
//            )
//
//            LottieAnimation(
//                composition = composition,
//                modifier = Modifier
//                    .constrainAs(image) { centerTo(parent) }
//                    .size(300.dp),
//                iterations = LottieConstants.IterateForever,
//            )
//        }
//
//        OnboardingButton(
//            onClick = onNext,
//            title = stringResource(id = R.string.button_next),
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 100.dp)
//        )
//    }
//}
//
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
//        OnBoardingTaskChatScreen {}
//    }
//}