package co.softov.morestuff.android.ui.onboarding

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.nav.OnBoarding
import co.softov.morestuff.android.ui.navigation.ChildPages
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.selectNext

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
            OnBoarding.Welcome -> WelcomeComposable(onNext = navigation::selectNext)
            OnBoarding.NotificationPermission -> NotificationPermissionComposable(onNext = navigation::selectNext)
            OnBoarding.WorkSpaceReady -> WorkSpaceReadyComposable(onFinish = onBoardingComplete)
        }
    }
}


@Composable
fun WelcomeComposable(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF272835)),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 250.dp, bottom = 30.dp, start = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_on_boarding),
                contentDescription = "app welcome image",
                modifier = Modifier
                    .shadow(
                        elevation = 9.137930870056152.dp,
                        spotColor = Color(0x6E000000),
                        ambientColor = Color(0x6E000000)
                    ),
                contentScale = ContentScale.None
            )

            Text(
                text = "Out goal ",
                style = TextStyle(
                    fontSize = 62.14.sp,
                    lineHeight = 88.77.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(900),
                    color = Color(0xFF898BA8),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.62.sp,
                )
            )

            Text(
                text = "is to help you plan and",
                style = TextStyle(
                    fontSize = 16.48.sp,
                    lineHeight = 23.55.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.16.sp,
                )
            )

            Text(
                text = "fulfill your tasks.",
                style = TextStyle(
                    fontSize = 16.48.sp,
                    lineHeight = 23.55.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.16.sp,
                )
            )
        }

        Row(
            Modifier.padding(bottom = 56.dp)
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
                        text = "Start",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 28.sp,
                            fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF272835),
                        )
                    )
                }
            )

        }
    }
}


@Composable
fun NotificationPermissionComposable(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF272835)),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 74.dp, bottom = 30.dp, start = 45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Notification",
                style = TextStyle(
                    fontSize = 40.51.sp,
                    lineHeight = 44.38.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(900),
                    color = Color(0xFF898BA8),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.41.sp,
                )
            )
            Text(
                text = "permission ",
                style = TextStyle(
                    fontSize = 40.51.sp,
                    lineHeight = 44.38.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(900),
                    color = Color(0xFF898BA8),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.41.sp,
                )
            )
            Spacer(modifier = Modifier.padding(15.dp))
            Text(
                text = "More Stuff requires Notification",
                style = TextStyle(
                    fontSize = 16.48.sp,
                    lineHeight = 23.55.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.16.sp,
                )
            )

            Text(
                text = "permission to send you Task &",
                style = TextStyle(
                    fontSize = 16.48.sp,
                    lineHeight = 23.55.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.16.sp,
                )
            )
            Text(
                text = "Reminder notifications",
                style = TextStyle(
                    fontSize = 16.48.sp,
                    lineHeight = 23.55.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(500),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.16.sp,
                )
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.permission_on_boarding),
                contentDescription = "notifications",
                contentScale = ContentScale.FillBounds
            )
        }

        Row(
            Modifier.padding(bottom = 56.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Column {
                Button(
                    onClick = { onNext() },
                    modifier = Modifier
                        .width(187.dp)
                        .height(43.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC8C)),
                    content = {
                        Text(
                            text = "Enable",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
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
                            text = "Skip",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
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
fun WorkSpaceReadyComposable(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF272835)),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 74.dp, bottom = 30.dp, start = 2.dp, end = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Your workspace",
                style = TextStyle(
                    fontSize = 40.51.sp,
                    lineHeight = 44.38.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(900),
                    color = Color(0xFF898BA8),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.41.sp,
                )
            )
            Text(
                text = "is ready",
                style = TextStyle(
                    fontSize = 40.51.sp,
                    lineHeight = 44.38.sp,
                    fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
                    fontWeight = FontWeight(900),
                    color = Color(0xFF898BA8),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.41.sp,
                )
            )

            Spacer(modifier = Modifier.padding(20.dp))
            Image(
                painter = painterResource(id = R.drawable.final_on_boarding),
                contentDescription = "notifications",
                contentScale = ContentScale.FillBounds
            )
        }

        Row(
            Modifier.padding(bottom = 56.dp)
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
                            text = "Start",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                fontFamily = FontFamily(Font(R.font.montserrat_semibold)),
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
