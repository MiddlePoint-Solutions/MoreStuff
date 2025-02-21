package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PriorityItem(
  task: TaskUiModel,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  enabled: Boolean = true,
  isDragging: Boolean = false,
  handleModifier: Modifier = Modifier
) {
  val offsetX by animateDpAsState(
    targetValue = if (selected) 32.dp else 0.dp,
    label = "PriorityItemOffset"
  )

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(80.dp)
      .background(color = MaterialTheme.colorScheme.surfaceContainer)
      .combinedClickable(enabled = enabled, onClick = onClick, onLongClick = onLongClick)
  ) {
    Row(
      modifier = modifier
        .fillMaxSize()
        .padding(start = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // Ícono de menú para arrastrar
      Box(
        modifier = handleModifier
          .padding(end = 8.dp)
          .size(24.dp)
      ) {
        if (selected && !isDragging) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Reorder",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxSize()
          )
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

          if (selected) {
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
      }
    }

    TaskItemBadges(
      task = task,
      modifier = Modifier.align(Alignment.BottomEnd)
    )
  }
}