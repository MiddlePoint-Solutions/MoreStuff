package co.softov.morestuff.android.ui.main.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.unit.dp
import app.cash.molecule.RecompositionClock.ContextClock
import app.cash.molecule.launchMolecule
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.presentation.PriorityModel
import co.softov.morestuff.android.presentation.PriorityOptionsPresenter
import co.softov.morestuff.android.presentation.PriorityPresenter
import co.softov.morestuff.android.ui.main.MainPresenter
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

    val optionsModel by scope.launchMolecule(clock = ContextClock) {
        PriorityOptionsPresenter()
    }.collectAsState()

    Surface(shadowElevation = 9.dp) {
        Column(modifier = modifier) {

            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(5.dp)
            ) {
                PriorityOptions(
                    model = optionsModel,
                    onOptionSelected = viewModel::onPriorityOptionChanged
                )
            }

            UserPriorityInput(
                currentPriority = when (model) {
                    is PriorityModel.Later -> Priority.Later()
                    is PriorityModel.Today -> Priority.Today()
                    is PriorityModel.Tomorrow -> Priority.Tomorrow()
                },
                onPrioritySelected = viewModel::priorityChanged
            )

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

val Priority.title: String
    @Composable get() = when (this) {
        is Priority.Today -> stringResource(id = R.string.priority_today)
        is Priority.Tomorrow -> stringResource(id = R.string.priority_tomorrow)
        is Priority.Later -> stringResource(id = R.string.priority_later)
    }
