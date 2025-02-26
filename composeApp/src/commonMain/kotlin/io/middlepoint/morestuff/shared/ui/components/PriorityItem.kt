package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import io.middlepoint.morestuff.shared.ui.utils.ReorderHapticFeedbackType
import io.middlepoint.morestuff.shared.ui.utils.SharedFunctionsHandler
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PriorityItem(
  task: TaskUiModel,
  onClick: () -> Unit,
  onTaskComplete: (Boolean) -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  hasSelection: Boolean = false,
  enabled: Boolean = true,
  handleModifier: Modifier = Modifier,
) {

  val sharedFunctionsHandler: SharedFunctionsHandler = koinInject()
  val haptic = sharedFunctionsHandler.rememberReorderHapticFeedback()

  /*val offsetX by animateDpAsState(
    targetValue = if (hasSelection) 4.dp else 0.dp,
    label = "PriorityItemOffset"
  )*/

  var isCompleted by remember { mutableStateOf(task.isComplete) }


  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .combinedClickable(
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
      Crossfade(
        targetState = hasSelection,
        label = "ToggleTaskAsDone or DragHandle transition"
      ) { showDragHandle ->
        if (showDragHandle) {
          Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = "Reorder",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = handleModifier
              .size(30.dp)
          )
        } else {
          ToggleTaskAsDone(
            selectedToComplete = isCompleted,
            onClick = {
              isCompleted = !isCompleted
              onTaskComplete(isCompleted)
            },
            enabled = enabled,
          )
        }
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
          textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
        )
      )
      if (selected) {
        AnimatedVisibility(
          visible = selected,
          enter = scaleIn() + fadeIn(),
          exit = scaleOut() + fadeOut()
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
              .padding(start = 30.dp, end = 16.dp)
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
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .size(30.dp)
      .clickable(enabled = enabled) {
        onClick() // Llamamos a onClick sin mantener un estado interno
      }
  ) {
    AnimatedContent(
      targetState = selectedToComplete, // Ahora depende completamente del estado externo
      label = "Toggle task completion animation",
      transitionSpec = { scaleIn() togetherWith fadeOut() },
      modifier = Modifier.align(Alignment.Center)
    ) { completed ->
      when (completed) {
        true -> {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Completed Task",
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }

        false -> {
          Icon(
            imageVector = Icons.Outlined.Circle,
            contentDescription = "Incomplete Task",
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}



