package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffHomeScaffold(
    snackbarHostState: SnackbarHostState,
    topAppBarScrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    content: @Composable (PaddingValues) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                SwipeToDismiss(
                    state = rememberDismissState(),
                    background = {},
                    dismissContent = { Snackbar(snackbarData = data) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        content = content,
        topBar = topBar
    )
}

