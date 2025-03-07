package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
  onTaskClick: () -> Unit,
  onTaskComplete: (Boolean) -> Unit,
  enabled: Boolean = true,
  onTaskLongPress: () -> Unit,
  modifier: Modifier = Modifier,
  isSelected: Boolean = false,
  isReorderModeActive: Boolean = false,
  handleModifier: Modifier = Modifier,
) {

  val sharedFunctionsHandler: SharedFunctionsHandler = koinInject()
  val haptic = sharedFunctionsHandler.rememberReorderHapticFeedback()

  val offsetX by animateDpAsState(
    targetValue = if (isReorderModeActive) 4.dp else 0.dp,
    label = "PriorityItemOffset"
  )

  var isCompleted by remember { mutableStateOf(task.isComplete) }


  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .combinedClickable(
        onClick = onTaskClick,
        onLongClick = {
          haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
          onTaskLongPress()
        })
  ) {
    Row(
      modifier = modifier
        .fillMaxSize()
        .padding(start = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {

      if (isReorderModeActive) {
        AnimatedVisibility(
          visible = isReorderModeActive,
          enter = slideInHorizontally(
            initialOffsetX = { -40 }
          ) + fadeIn(animationSpec = tween(1000)),
          exit = slideOutHorizontally(
            targetOffsetX = { 40 }
          ) + fadeOut(animationSpec = tween(1000))
        ) {
          Box(
            modifier = handleModifier
              .padding(end = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.DragHandle,
              contentDescription = "Reorder",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(30.dp)
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
          TaskProfile(task.title)
          androidx.compose.animation.AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)),
            modifier = Modifier.align(Alignment.BottomEnd)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .align(Alignment.BottomEnd)
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
        val title by remember { derivedStateOf { task.title } }

        Text(
          text = title,
          modifier = Modifier
            .fillMaxWidth(0.85f)
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


      }

    }
    Column(
      modifier = Modifier
        .align(if (isSelected || isReorderModeActive) Alignment.CenterEnd else Alignment.BottomEnd)
        .padding(end = 8.dp)
        .width(48.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      AnimatedContent(
        targetState = isSelected || isReorderModeActive,
        transitionSpec = {
          (fadeIn() + slideInVertically()).togetherWith(fadeOut() + slideOutVertically())
        },
        label = "Task Toggle Transition"
      ) { showToggleTaskAsDone ->
        if (showToggleTaskAsDone) {
          ToggleTaskAsDone(
            selectedToComplete = isCompleted,
            onClick = {
              isCompleted = !isCompleted
              onTaskComplete(isCompleted)
            },
            enabled = enabled,
          )
        } else {
          TaskItemBadges(task = task)
        }
      }
    }
  }
}


@Composable
fun ToggleTaskAsDone(
  selectedToComplete: Boolean,
  onClick: () -> Unit,
  enabled: Boolean = true,
  modifier: Modifier = Modifier
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(30.dp)
      .clickable(enabled = enabled) { onClick() }
  ) {
    AnimatedContent(
      targetState = selectedToComplete,
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

