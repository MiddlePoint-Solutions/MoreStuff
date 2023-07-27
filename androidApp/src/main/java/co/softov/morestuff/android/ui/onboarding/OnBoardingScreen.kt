package co.softov.morestuff.android.ui.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.nav.OnBoarding
import co.softov.morestuff.android.ui.navigation.ChildPages
import co.softov.morestuff.android.ui.priority.PriorityButton
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
    val pages = remember { listOf(OnBoarding.Welcome, OnBoarding.NotificationPermission) }

    ChildPages(
        source = navigation,
        modifier = Modifier.fillMaxSize(),
        initialPages = {
            Pages(items = pages, selectedIndex = 0)
        }
    ) { screen ->
        when (screen) {
            OnBoarding.Welcome -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = 30.dp
                        )
                ) {
                    Text(
                        text = "Welcome!!!",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )

                    PriorityButton(
                        onClick = {
                            navigation.selectNext()
                        },
                        modifier = Modifier.align(Alignment.BottomCenter),
                        text = "Next"
                    )
                }
            }

            OnBoarding.NotificationPermission -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = 30.dp
                        )
                ) {
                    Text(
                        text = "NotificationPermission!!!",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )

                    PriorityButton(
                        onClick = {
                            onBoardingComplete()
                        },
                        modifier = Modifier.align(Alignment.BottomCenter),
                        text = "Finish"
                    )
                }
            }
        }
    }

}