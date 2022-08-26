package co.softov.morestuff.android.ui.main.input

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import app.cash.molecule.RecompositionClock
import app.cash.molecule.RecompositionClock.ContextClock
import app.cash.molecule.launchMolecule
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.presentation.PriorityModel
import co.softov.morestuff.android.presentation.PriorityPresenter
import co.softov.morestuff.android.ui.main.MainPresenter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
fun UserInput(
    viewModel: MainPresenter,
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit,
    resetScroll: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val priorityModel: StateFlow<PriorityModel> = scope.launchMolecule(clock = ContextClock) {
        PriorityPresenter()
    }

    val model by priorityModel.collectAsState()

    Column(
        modifier = modifier
    ) {
        UserPriorityInput(
            currentPriority = when (model) {
                is PriorityModel.Later -> Priority.Later()
                is PriorityModel.Today -> Priority.Today()
                is PriorityModel.Tomorrow -> Priority.Tomorrow()
            }
        ) {
            viewModel.priorityChanged(it)
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
