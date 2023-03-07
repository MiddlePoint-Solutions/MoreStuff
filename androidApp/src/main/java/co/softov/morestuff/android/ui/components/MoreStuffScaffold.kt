package co.softov.morestuff.android.ui.components


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.*
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import co.softov.morestuff.android.ui.Drawer.DrawerLayout
import kotlinx.coroutines.CoroutineScope

@SuppressLint("SuspiciousIndentation")
@Composable
fun MoreStuffScaffold(
    drawerState: DrawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed),
    scaffoldState: ScaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed)),
    content: @Composable (PaddingValues) -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    onItemClicked: (String) -> Unit,
    ) {
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
                    scaffoldState = scaffoldState,
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