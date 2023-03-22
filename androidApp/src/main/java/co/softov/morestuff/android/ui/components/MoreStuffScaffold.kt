package co.softov.morestuff.android.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.drawer.DrawerLayout
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
                        onSettingsClicked()
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
                            drawerState = drawerState,
                        )
                    },
                    content = content,
                )
            }
        )
    }
}