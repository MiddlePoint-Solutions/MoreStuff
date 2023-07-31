package co.softov.morestuff.android.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.enums.Filter
import co.softov.morestuff.android.ui.drawer.DrawerLayout
import co.softov.morestuff.android.ui.search.CustomSearchBar
import co.softov.morestuff.android.ui.search.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffHomeScaffold(
    snackbarHostState: SnackbarHostState,
    showSettings: () -> Unit,
    showReview: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    showTaskChat: (taskId: Long) -> Unit,
) {
    val viewModel: SearchViewModel = getViewModel()
    var isSearching by remember { mutableStateOf(false) }
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope: CoroutineScope = rememberCoroutineScope()
    val offsetX by animateDpAsState(if (isSearching) 500.dp else 0.dp, label = "")

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
                    Box {
                        MoreStuffTopBar(
                            reviewSelected = showReview,
                            settingsSelected = showSettings,
                            searchSelected = { isSearching = true }
                        )

                        AnimatedVisibility(
                            visible = isSearching,
                            enter = slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                            exit = slideOutHorizontally(

                                targetOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = LinearOutSlowInEasing
                                )
                            )
                        ) {
                            CustomSearchBar(
                                onSearchClose = { isSearching = false },
                                showTaskChat = showTaskChat,
                                modifier = Modifier
                                    .offset(x = offsetX)
                                    .fillMaxWidth()
                            )
                        }
                    }
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        content(PaddingValues(it.calculateTopPadding()))

                        if (isSearching) {
                            BackHandler {
                                isSearching = false
                                viewModel.searchResults.value = listOf()
                                viewModel.setFilter(Filter.None)
                            }
                        }
                    }
                }
            )
        }
    )
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffHomeScaffold(
    snackbarHostState: SnackbarHostState,
    showSettings: () -> Unit,
    showReview: () -> Unit,

    content: @Composable (PaddingValues) -> Unit,
    showTaskChat: (taskId: Long) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
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
                    if (!isSearching) {
                        MoreStuffTopBar(
                            reviewSelected = showReview,
                            settingsSelected = showSettings,
                            searchSelected = { isSearching = true }
                        )
                    }
                },
                content = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        content(PaddingValues(it.calculateTopPadding()))

                        if (isSearching) {
                            BackHandler {
                                isSearching = false
                            }
                            CustomSearchBar(
                                onSearchClose = { isSearching = false },
                                showTaskChat = showTaskChat,
                            )
                        }
                    }
                }
            )
        }
    )
}*/
