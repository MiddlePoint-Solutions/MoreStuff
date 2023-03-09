package co.softov.morestuff.android.ui.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.presentation.PriorityOptionsModel
import co.softov.morestuff.android.ui.components.PermissionRequester
import co.softov.morestuff.android.ui.main.MainViewModel

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
fun UserInput(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit,
    resetScroll: () -> Unit
) {
    Surface(shadowElevation = 9.dp) {
        Column(modifier = modifier) {
            PermissionRequester()
            UserTextInput(
                listAction = viewModel::showTaskList,
                sendAction = {
                    resetScroll()
                    onMessageSent(it)
                }
            )
        }
    }
}
