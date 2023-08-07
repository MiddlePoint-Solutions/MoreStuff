package co.softov.morestuff.android.ui.onboarding

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.selectNext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun OnBoardingContent(
    onBoardingComplete: () -> Unit,
) {
    val navigation = remember { PagesNavigation<OnBoarding>() }
    val pages = remember {
        listOf(
            OnBoarding.Welcome,
            OnBoarding.NotificationPermission,
            OnBoarding.WorkSpaceReady
        )
    }

    ChildPages(
        source = navigation,
        modifier = Modifier.fillMaxSize(),
        initialPages = {
            Pages(items = pages, selectedIndex = 0)
        }
    ) { screen ->
        when (screen) {
            OnBoarding.Welcome -> WelcomeScreen(onNext = navigation::selectNext)
            OnBoarding.NotificationPermission -> NotificationPermissionScreen(onNext = navigation::selectNext)
            OnBoarding.WorkSpaceReady -> ReadyScreen(onFinish = onBoardingComplete)
        }
    }
}


@Composable
private fun WelcomeScreen(onNext: () -> Unit) {
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
                painter = painterResource(id = R.drawable.ic_on_boarding),
                contentDescription = "app welcome image",
                contentScale = ContentScale.None
            )

            Text(
                text = stringResource(R.string.out_goal),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = stringResource(R.string.is_to_help_you_plan_and),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Row(
            Modifier
                .padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Button(
                onClick = { onNext() },
                modifier = Modifier
                    .width(287.dp)
                    .height(43.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC8C)),
                content = {
                    Text(
                        text = stringResource(R.string.button_start),
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF272835),
                        )
                    )
                }
            )

        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationPermissionScreen(onNext: () -> Unit) {

    val permissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
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
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = stringResource(R.string.more_stuff_requires_notification),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.permission_on_boarding),
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
                    onClick = {
                        permissionState.launchPermissionRequest()
                        onNext()
                    },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC8C)),
                    content = {
                        Text(
                            text = stringResource(R.string.button_enable),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF272835),
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9093B4)),
                    content = {
                        Text(
                            text = stringResource(R.string.button_skip),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF272835),
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ReadyScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
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
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.final_on_boarding),
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC8C)),
                    content = {
                        Text(
                            text = stringResource(R.string.button_start),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF272835),
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
