package co.softov.morestuff.android.ui.priority

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.presentation.PriorityModel
import co.softov.morestuff.android.presentation.PriorityOptionsModel
import co.softov.morestuff.android.presentation.PriorityOptionsPresenter
import co.softov.morestuff.android.presentation.PriorityPresenter
import co.softov.morestuff.android.ui.main.input.PriorityOptions
import co.softov.morestuff.android.ui.main.input.UserPriorityInput
import org.koin.androidx.compose.get

@Composable
fun PriorityInput(
    modifier: Modifier = Modifier,
    viewModel: PriorityViewModel = get()
) {

    val scope = rememberCoroutineScope()
    val priority by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
        PriorityPresenter()
    }.collectAsState()

    val priorityOptions by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
        PriorityOptionsPresenter()
    }.collectAsState()

    Column(modifier = modifier) {
        UserPriorityOptionsInput(
            priorityOptions,
            viewModel::onPriorityOptionChanged
        )

        UserPriorityInput(
            currentPriority = when (priority) {
                is PriorityModel.Later -> Priority.Later()
                is PriorityModel.Today -> Priority.Today()
                is PriorityModel.Tomorrow -> Priority.Tomorrow()
            },
            onPrioritySelected = viewModel::priorityChanged
        )
    }
}

@Composable
fun UserPriorityOptionsInput(
    optionsModel: PriorityOptionsModel,
    onOptionSelected: (PriorityOption) -> Unit = {}
) {
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
            onOptionSelected = onOptionSelected
        )
    }
}