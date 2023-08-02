package co.softov.morestuff.android.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.findViewTreeLifecycleOwner
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.compose.ProvideLocalViewModelStoreOwner
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.search.SearchBar
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffHomeScaffold(
    snackbarHostState: SnackbarHostState,
    content: @Composable (PaddingValues) -> Unit,
) {

    val navigation = LocalAppNavigation.current

    var isSearching by rememberSaveable { mutableStateOf(false) }
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope: CoroutineScope = rememberCoroutineScope()

    val closeDrawer = remember {
        { scope.launch { drawerState.close() } }
    }

    val dismissSnackbarState = rememberDismissState(
        confirmValueChange = { _ ->
            snackbarHostState.currentSnackbarData?.dismiss()
            true
        }
    )

    BackHandler(enabled = drawerState.isOpen) {
        closeDrawer()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                ModalDrawerSheet {}
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
                                reviewSelected = { navigation.push(Screen.Review) },
                                settingsSelected = { navigation.push(Screen.Settings) },
                                searchSelected = { isSearching = true }
                            )
                        }
                    },
                    content = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            content(PaddingValues(it.calculateTopPadding()))
                        }
                    }
                )
            }
        )

        AnimatedVisibility(
            visible = isSearching,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            SearchBar(
                onSearchClose = { isSearching = false },
                showTaskChat = { navigation.push(Screen.TaskChat(it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

