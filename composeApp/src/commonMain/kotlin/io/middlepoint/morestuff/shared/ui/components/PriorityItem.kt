package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
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
  isInSearch: Boolean = false,
  isReorderModeActive: Boolean = false,
  handleModifier: Modifier = Modifier,
) {
  val sharedFunctionsHandler: SharedFunctionsHandler = koinInject()
  val haptic = sharedFunctionsHandler.rememberReorderHapticFeedback()

  var isCompleted by remember { mutableStateOf(task.isComplete) }
  val interactionSource = remember { MutableInteractionSource() }


  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .combinedClickable(
        interactionSource = interactionSource,
        indication = if (enabled) LocalIndication.current else null,
        onClick = onTaskClick,
        onLongClick = {
          if (enabled) {
            haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
            onTaskLongPress()
          }
        }
      )
  ) {
    Row(
      modifier = modifier
        .fillMaxSize()
        .padding(start = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {

      if (!isInSearch) {
        ToggleTaskComplete(
          selectedToComplete = isCompleted,
          onClick = {
            isCompleted = !isCompleted
            onTaskComplete(isCompleted)
          },
          modifier = Modifier.onPreviewKeyEvent { false },
          enabled = enabled
        )
      }

      Spacer(modifier = Modifier.width(8.dp))
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
          fontWeight = FontWeight(500),
          color = Color(0xFFFFFFFF),
          textAlign = TextAlign.Start,
          textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
        )
      )
    }

    val badgesAlpha by animateFloatAsState(
      targetValue = if (isSelected || isReorderModeActive) 0f else 1f,
      animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
      label = "BadgesAlphaAnimation"
    )

    val toggleAlpha by animateFloatAsState(
      targetValue = if (isSelected || isReorderModeActive) 1f else 0f,
      animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
      label = "ToggleAlphaAnimation"
    )

    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .width(48.dp)
        .height(48.dp)
        .graphicsLayer {
          alpha = badgesAlpha
        },
      contentAlignment = Alignment.BottomEnd
    ) {
      TaskItemBadges(
        modifier = Modifier.fillMaxSize(),
        task = task
      )
    }

    Box(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .width(48.dp)
        .height(48.dp)
        .graphicsLayer {
          alpha = toggleAlpha
        },
      contentAlignment = Alignment.Center
    ) {
      if (isReorderModeActive) {
        Box(
          modifier = handleModifier
            .size(48.dp)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null,
              onClick = { }
            ),
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
    HorizontalDivider(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth(),
      thickness = 0.7.dp,
      color = MaterialTheme.colorScheme.outlineVariant
    )
  }
}


@Composable
fun ToggleTaskComplete(
  selectedToComplete: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
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
            modifier = Modifier.size(26.dp),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }

        false -> {
          Icon(
            imageVector = Icons.Outlined.Circle,
            contentDescription = "Incomplete Task",
            modifier = Modifier.size(26.dp),
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}

