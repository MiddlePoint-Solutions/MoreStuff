package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.home.PriorityInputModel
import co.softov.morestuff.android.ui.home.PriorityModel

@Composable
fun PriorityInput(
    model: PriorityInputModel,
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
    model: PriorityInputModel,
    modifier: Modifier = Modifier,
    onNowSelected: () -> Unit = {},
    onLaterSelected: () -> Unit = {},
    onPlanSelected: () -> Unit = {},
    onTimeChange: (Int, Int) -> Unit = { _, _ -> },
    onDateChange: (Long) -> Unit = {},
) {
    Column(
        modifier = modifier.background(color = Color.Transparent)
    ) {

        val showPlanInput by remember(model.priority) {
            derivedStateOf { model.priority is PriorityModel.Plan }
        }

        AnimatedVisibility(
            showPlanInput,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { (it * 1.5).toInt() }
        ) {

            Box(
                modifier = modifier.fillMaxWidth()
            ) {

                PlanPrioritySelector(
                    model = model,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onDateChange = onDateChange,
                    onTimeChange = onTimeChange
                )
            }
        }
    }

    Surface(
        shadowElevation = 12.dp,
        tonalElevation = 5.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ) {
        PrioritySelector(
            priority = model.priority,
            onNowSelected = onNowSelected,
            onLaterSelected = onLaterSelected,
            onPlanSelected = onPlanSelected,
        )
    }
}