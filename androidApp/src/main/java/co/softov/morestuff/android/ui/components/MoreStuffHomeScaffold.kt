package co.softov.morestuff.android.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.softov.morestuff.android.ui.drawer.DrawerLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffHomeScaffold(
    snackbarHostState: SnackbarHostState,
    showSettings: () -> Unit,
    showReview: () -> Unit,
    isSearching: MutableState<Boolean>,
    searchText: MutableState<String>,
    content: @Composable (PaddingValues) -> Unit,
    showTaskChat: (taskId: Long) -> Unit,
) {
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope: CoroutineScope = rememberCoroutineScope()

    val closeDrawer: () -> Unit = remember {
        { scope.launch { drawerState.close() } }
    }

    val dismissSnackbarState = rememberDismissState(
        confirmValueChange = { _ ->
            snackbarHostState.currentSnackbarData?.dismiss()
            true
        }
    )

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
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        SwipeToDismiss(
                            state = dismissSnackbarState,
                            background = {},
                            dismissContent = { Snackbar(snackbarData = data) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                topBar = {
                    if (!isSearching.value) {
                        MoreStuffTopBar(
                            reviewSelected = showReview,
                            settingsSelected = showSettings,
                            searchSelected = { isSearching.value = true }
                        )
                    }
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        content(PaddingValues(it.calculateTopPadding()))

                        if (isSearching.value) {
                            CustomSearchBar(
                                searchText = searchText,
                                onSearchClose = { isSearching.value = false },
                                isSearching =isSearching,
                                showTaskChat = showTaskChat,
                            )
                        }
                    }
                }
            )
        }
    )
}