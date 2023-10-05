package co.softov.morestuff.android.ui.onboarding

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.nav.OnBoarding
import co.softov.morestuff.android.ui.navigation.ChildPages
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.selectNext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun OnBoardingScreen(
    onBoardingComplete: () -> Unit,
) {
    val navigation = remember { PagesNavigation<OnBoarding>() }
    val pages = remember {
        buildList {
            add(OnBoarding.Welcome)
            if (requiresNotificationsPermission()) {
                add(OnBoarding.NotificationPermission)
                add(OnBoarding.ChatWithYourTask)
                add(OnBoarding.WorkSpaceReady)
            }
        }
    }

    ChildPages(
        source = navigation,
        modifier = Modifier.fillMaxSize(),
        initialPages = { Pages(items = pages, selectedIndex = 0) }
    ) { screen ->
        when (screen) {
            is OnBoarding.Welcome -> {
                WelcomeScreen(
                    nextButton = {
                        Button(
                            onClick = {
                                if (requiresNotificationsPermission()) {
                                    navigation.selectNext()
                                } else {
                                    onBoardingComplete()
                                }
                            },
                            modifier = Modifier
                                .width(287.dp)
                                .height(43.dp),
                            content = {
                                Text(
                                    text = stringResource(R.string.button_start),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 28.sp,
                                        fontWeight = FontWeight(700),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                )
                            }
                        )
                    }
                )
            }

            OnBoarding.NotificationPermission -> NotificationPermissionScreen(onNext = navigation::selectNext)
            OnBoarding.ChatWithYourTask -> ChatWithYourTaskScreen(
                modifier = Modifier,
                onNext = navigation::selectNext
            )

            OnBoarding.WorkSpaceReady -> ReadyScreen(onFinish = onBoardingComplete)

        }
    }
}


@Composable
private fun WelcomeScreen(
    nextButton: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "app welcome image",
                contentScale = ContentScale.None
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.our_goal),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = stringResource(R.string.is_to_help_you_plan_and),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        Box(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            nextButton()
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermissionScreen(onNext: () -> Unit) {

    val permissionState = if (requiresNotificationsPermission()) {
        rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        ) { granted ->
            if (granted) {
                onNext()
            }
        }
    } else {
        error("VERSION.SDK_INT < TIRAMISU")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.notification),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = stringResource(R.string.more_stuff_requires_notification),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.notification_permission),
                contentDescription = "Notifications permission image",
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = {
                        permissionState.launchPermissionRequest()
                    },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_enable),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Button(
                    onClick = { onNext() },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    content = {
                        Text(
                            text = stringResource(R.string.button_skip),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        )
                    }
                )
            }
        }
    }
}

private fun requiresNotificationsPermission() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@Composable
private fun ReadyScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.your_workspace),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.onboarding_workspace),
                contentDescription = "notifications",
                contentScale = ContentScale.FillBounds
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = { onFinish() },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_start),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )

            }
        }
    }
}

@Composable
private fun ChatWithYourTaskScreen(modifier: Modifier, onNext: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_lncy8nut))
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.chat_task),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.save_details_chat),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            LottieAnimation(
                modifier = modifier.size(380.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = { onNext() },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    content = {
                        Text(
                            text = stringResource(R.string.button_next),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    }
                )

            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
private fun Preview() {
    MoreStuffTheme {
        Column {
            WelcomeScreen {}
            NotificationPermissionScreen {}
            ReadyScreen {}
        }
    }
}
