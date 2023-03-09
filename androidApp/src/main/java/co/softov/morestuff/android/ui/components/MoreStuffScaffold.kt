package co.softov.morestuff.android.ui.components

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

@Composable
fun MoreStuffScaffold(
    content: @Composable (PaddingValues) -> Unit,
    onItemClicked: (String) -> Unit,
) {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope: CoroutineScope = rememberCoroutineScope()

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