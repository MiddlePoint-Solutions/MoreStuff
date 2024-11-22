package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffHomeScaffold(
    snackbarHostState: SnackbarHostState,
    content: @Composable (PaddingValues) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    Scaffold(
        containerColor = Color.Transparent, // TODO: this should be set to a solid color
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                SwipeToDismissBox(
                    state = rememberSwipeToDismissBoxState(),
                    backgroundContent = {},
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Snackbar(snackbarData = data)
                    }
                )
            }
        },
        content = content,
        topBar = topBar,
    )
}

