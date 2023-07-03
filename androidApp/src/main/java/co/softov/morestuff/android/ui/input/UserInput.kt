package co.softov.morestuff.android.ui.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.components.PermissionRequester
import co.softov.morestuff.android.ui.priority.PriorityInput
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    initialValue: String = "",
    priorityContent: @Composable () -> Unit = {},
    showTaskLists: () -> Unit = {},
    onSubmitInput: (String) -> Unit = {},
) {
    Column(modifier.imePadding()) {

        priorityContent()

        Surface(shadowElevation = 9.dp) {
            Column(modifier = modifier) {
                PermissionRequester()
                UserTextInput(
                    initialValue = initialValue,
                    listAction = showTaskLists,
                    sendAction = onSubmitInput,
                )
            }
        }
    }
}