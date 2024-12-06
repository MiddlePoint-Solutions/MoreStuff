package io.middlepoint.morestuff.shared.ui.components.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.ui.components.input.UserInputEvent
import io.middlepoint.morestuff.shared.ui.model.PriorityUiModel
import io.middlepoint.morestuff.shared.ui.model.ScheduleUiModel

@Composable
fun PriorityInput(
  priority: PriorityUiModel,
  schedule: ScheduleUiModel?,
  onEvent: (UserInputEvent) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
  ) {

    val showPlanInput = remember(priority) { priority is PriorityUiModel.Plan }

    LaunchedEffect(schedule) {
      Logger.d { "Yoooo: $schedule" }
    }

    AnimatedVisibility(
      showPlanInput,
      enter = slideInVertically { it * 2 },
      exit = slideOutVertically { (it * 1.5).toInt() }
    ) {

      LaunchedEffect(Unit) {
        Logger.d { "Yoooo1111: $schedule" }
      }

      Box(
        modifier = Modifier.size(200.dp).background(color = MaterialTheme.colorScheme.error),
        contentAlignment = Alignment.Center
      ) {
        if (schedule != null) { // TODO: why is this null?
          LaunchedEffect(Unit) {
            Logger.d { "Yoooo2222: $schedule" }
          }
          PlanPrioritySelector(
            scheduleModel = schedule,
            modifier = Modifier.fillMaxWidth(),
            onDateChange = { onEvent(UserInputEvent.UpdatePlanDate(it)) },
            onTimeChange = { h, m -> onEvent(UserInputEvent.UpdatePlanTime(h, m)) }
          )
        }
      }
    }
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    color = MaterialTheme.colorScheme.surfaceVariant
  ) {
    PrioritySelector(
      priority = priority,
      onNowSelected = { onEvent(UserInputEvent.SetNowPriority) },
      onLaterSelected = { onEvent(UserInputEvent.SetLaterPriority) },
      onPlanSelected = { onEvent(UserInputEvent.SetPlanPriority) },
    )
  }
}