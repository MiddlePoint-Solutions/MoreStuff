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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    topAppBarScrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    content: @Composable (PaddingValues) -> Unit,
) {

    val navigation = LocalAppNavigation.current
    var isSearching by rememberSaveable { mutableStateOf(false) }

    val dismissSnackbarState = rememberDismissState(
        confirmValueChange = { _ ->
            snackbarHostState.currentSnackbarData?.dismiss()
            true
        }
    )

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
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
            MoreStuffTopBar(
                reviewSelected = { navigation.push(Screen.Review) },
                settingsSelected = { navigation.push(Screen.Settings) },
                searchSelected = { isSearching = true },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        content = content
    )

    AnimatedVisibility(
        visible = isSearching,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        SearchBar(
            onSearchClose = { isSearching = false },
            showTaskChat = { navigation.push(Screen.TaskChat(it)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

