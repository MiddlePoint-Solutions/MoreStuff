package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.softov.morestuff.android.ui.model.PriorityInputUiModel
import co.softov.morestuff.android.ui.model.PriorityUiModel

@Composable
fun PriorityInput(
    model: PriorityInputUiModel,
    onNowSelected: () -> Unit,
    onLaterSelected: () -> Unit,
    onPlanSelected: () -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    PriorityInputContent(
        model = model,
        modifier = modifier,
        onNowSelected = onNowSelected,
        onLaterSelected = onLaterSelected,
        onPlanSelected = onPlanSelected,
        onTimeChange = onTimeChange,
        onDateChange = onDateChange,
    )
}

@Composable
private fun PriorityInputContent(
    model: PriorityInputUiModel,
    modifier: Modifier = Modifier,
    onNowSelected: () -> Unit = {},
    onLaterSelected: () -> Unit = {},
    onPlanSelected: () -> Unit = {},
    onTimeChange: (Int, Int) -> Unit = { _, _ -> },
    onDateChange: (Long) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {

        val showPlanInput by remember(model.priority) {
            derivedStateOf { model.priority is PriorityUiModel.Plan }
        }

        AnimatedVisibility(
            showPlanInput,
//            modifier = modifier.fillMaxWidth(),
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { (it * 1.5).toInt() }
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                PlanPrioritySelector(
                    model = model,
                    modifier = Modifier.fillMaxWidth(),
                    onDateChange = onDateChange,
                    onTimeChange = onTimeChange
                )
            }
        }
    }

    Surface(
//        modifier = modifier.zIndex(3f),
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        PrioritySelector(
            priority = model.priority,
            onNowSelected = onNowSelected,
            onLaterSelected = onLaterSelected,
            onPlanSelected = onPlanSelected,
        )
    }
}