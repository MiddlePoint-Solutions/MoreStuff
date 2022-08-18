package co.softov.morestuff.android.ui.main.input

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.tooling.preview.Preview
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.ui.main.MainViewModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
fun UserInput(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit,
    resetScroll: () -> Unit
) {
    val state by viewModel.priorityState.collectAsState()

    Column(
        modifier = modifier
    ) {
        UserPriorityInput(
            currentPriority = state
        ) {
            when (it) {
                is Priority.Later -> viewModel.selectedLaterPriority()
                is Priority.Today -> viewModel.selectedTodayPriority()
                is Priority.Tomorrow -> viewModel.selectedTomorrowPriority()
            }
        }

        UserTextInput(
            listAction = viewModel::showTaskList,
            sendAction = {
                resetScroll()
                onMessageSent(it)
            }
        )
    }
}

val Priority.title: String
    @Composable get() = when (this) {
        is Priority.Today -> stringResource(id = R.string.priority_today)
        is Priority.Tomorrow -> stringResource(id = R.string.priority_tomorrow)
        is Priority.Later -> stringResource(id = R.string.priority_later)
    }
