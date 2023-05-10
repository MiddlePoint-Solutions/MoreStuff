package co.softov.morestuff.android.ui.input

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.components.PermissionRequester

@Composable
fun UserInput(
    showTaskListAction: () -> Unit,
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit,
    resetScroll: () -> Unit,
) {
    Surface(shadowElevation = 9.dp) {
        Column(modifier = modifier) {
            PermissionRequester()
            UserTextInput(
                listAction = showTaskListAction,
                sendAction = {
                    resetScroll()
                    onMessageSent(it)
                },
            )
        }
    }
}
