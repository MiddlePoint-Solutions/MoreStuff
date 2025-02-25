package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.TaskColors
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.utils.ReorderHapticFeedbackType
import io.middlepoint.morestuff.shared.ui.utils.SharedFunctionsHandler
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PriorityItem(
  task: TaskUiModel,
  onClick: () -> Unit,
  onTaskComplete: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  selectedToComplete: Boolean = false,
  hasSelection: Boolean = false,
  enabled: Boolean = true,
  isDragging: Boolean = false,
  handleModifier: Modifier = Modifier,
) {

  val sharedFunctionsHandler: SharedFunctionsHandler = koinInject()
  val haptic = sharedFunctionsHandler.rememberReorderHapticFeedback()

  val offsetX by animateDpAsState(
    targetValue = if (hasSelection) 4.dp else 0.dp,
    label = "PriorityItemOffset"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .combinedClickable(
        enabled = enabled,
        onClick = onClick,
        onLongClick = {
          haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
          onLongClick()
        })
  ) {
    Row(
      modifier = modifier
        .fillMaxSize()
        .padding(start = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (hasSelection) {
        Box(
          modifier = handleModifier
            .padding(end = 8.dp)
            .size(24.dp)
        ) {
          if (!isDragging) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = "Reorder",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.fillMaxSize()
            )
          }

        }
      }

      Row(
        modifier = Modifier
          .offset(x = offsetX)
          .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box {
          //TaskProfile(task.title)
          ToggleTaskAsDone(
            selectedToComplete = selectedToComplete,
            onClick = onTaskComplete,
            enabled = enabled,
            taskTitle = task.title
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val title by remember { derivedStateOf { task.title } }

        Text(
          text = title,
          modifier = Modifier
            .weight(1f)
            .padding(12.dp),
          maxLines = 2,
          color = MaterialTheme.colorScheme.onSurface,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Start,
          )
        )
        if (selected) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
              .padding(start = 30.dp)
              .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
              )
              .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
              )
              .size(20.dp)
          )
        }
      }
    }

    TaskItemBadges(
      task = task,
      modifier = Modifier.align(Alignment.BottomEnd)
    )
  }
}


@Composable
fun ToggleTaskAsDone(
  selectedToComplete: Boolean,
  onClick: () -> Unit,
  enabled: Boolean = true,
  taskTitle: String,
) {
  var isCompleted by remember { mutableStateOf(selectedToComplete) }

  val profileColors by remember {
    derivedStateOf { TaskColors.getProfileColorsForTask(taskTitle) }
  }

  val gradientBrush = remember(profileColors) {
    Brush.verticalGradient(profileColors)
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .size(24.dp)
      .border(
        width = 1.dp,
        color = if (isCompleted)
          profileColors.first()
        else
          MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        shape = CircleShape
      )
      .padding(2.dp)
      .background(
        brush = if (isCompleted)
          gradientBrush
        else
          SolidColor(Color.Transparent),
        shape = CircleShape
      )
      .clickable(enabled = enabled) {
        isCompleted = !isCompleted
        onClick()
      }
  ) {}
}





