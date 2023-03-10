package co.softov.morestuff.android.ui.priority

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.presentation.model.PriorityModel
import co.softov.morestuff.android.presentation.model.PriorityOptionsModel
import co.softov.morestuff.android.ui.main.input.PriorityOptions
import org.koin.androidx.compose.get

@Composable
fun PriorityInput(
    priority: PriorityModel,
    priorityOptions: PriorityOptionsModel,
    modifier: Modifier = Modifier,
    viewModel: PriorityViewModel = get()
) {
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