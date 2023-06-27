package co.softov.morestuff.android.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.drawer.DrawerLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MoreStuffHomeScaffold(
    snackbarHostState: SnackbarHostState,
    showSettings: () -> Unit,
    showReview: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope: CoroutineScope = rememberCoroutineScope()

    val closeDrawer: () -> Unit = remember {
        { scope.launch { drawerState.close() } }
    }

    BackHandler(enabled = drawerState.isOpen) {
        if (drawerState.isOpen) {
            closeDrawer()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                DrawerLayout(
                    closeDrawer = closeDrawer,
                    showSettings = showSettings,
                    showPriorityReview = showReview
                )
            }
        },
        content = {
            Scaffold(
                modifier = Modifier.systemBarsPadding(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    MoreStuffTopBar(
                        openDrawer = { scope.launch { drawerState.open() } },
                        showReview = showReview,
                    )
                },
                content = content,
            )
        }
    )
}