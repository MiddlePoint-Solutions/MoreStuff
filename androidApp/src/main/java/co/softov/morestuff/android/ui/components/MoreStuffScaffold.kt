package co.softov.morestuff.android.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.drawer.DrawerLayout
import co.softov.morestuff.android.ui.settings.SettingsScreen
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MoreStuffScaffold(
    content: @Composable (PaddingValues) -> Unit,
    onSettingsClicked: () -> Unit,
) {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope: CoroutineScope = rememberCoroutineScope()
    val settingsVisible = remember { mutableStateOf(false) }

    BackHandler(enabled = settingsVisible.value) {
        settingsVisible.value = false
    }

    BackHandler(enabled = drawerState.isOpen) {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }
    }

    MoreStuffTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerLayout {
                        settingsVisible.value = true
                        scope.launch {
                            drawerState.close()
                        }
                    }
                }
            },
            content = {

                Scaffold(
                    modifier = Modifier.systemBarsPadding(),
                    topBar = {
                        MoreStuffTopBar(
                            scope = scope,
                            drawerState = drawerState,
                        )
                    },
                    content = content,
                )
            }
        )
        @OptIn(ExperimentalAnimationApi::class)
        AnimatedVisibility(
            visible = settingsVisible.value,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                SettingsScreen()

            }
        }
    }
}






