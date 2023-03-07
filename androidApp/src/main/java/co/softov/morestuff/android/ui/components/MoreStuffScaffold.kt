package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.Drawer.DrawerLayout
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.CoroutineScope

@Composable
fun MoreStuffScaffold(
    drawerState: DrawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed),
    content: @Composable (PaddingValues) -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    onItemClicked: (String) -> Unit,
    ) {
    MoreStuffTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerLayout(
                        onItemClicked = onItemClicked
                    )
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
    }
}