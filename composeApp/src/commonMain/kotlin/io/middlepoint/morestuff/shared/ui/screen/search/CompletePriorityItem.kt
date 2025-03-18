package io.middlepoint.morestuff.shared.ui.screen.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.components.TaskItemBadges
import io.middlepoint.morestuff.shared.ui.components.TaskProfile
import io.middlepoint.morestuff.shared.ui.model.TaskUiModel
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.completed
import org.jetbrains.compose.resources.stringResource

@Composable
fun CompletePriorityItem(
  task: TaskUiModel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isDragging: Boolean = false,
) {
  val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "").value
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(max = 90.dp)
      .shadow(elevation = elevation)
      .background(MaterialTheme.colorScheme.surfaceContainer)
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = modifier
        .fillMaxSize()
        .padding(start = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {

      TaskProfile(task.title)


      Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = task.title,
          textAlign = TextAlign.Start,
          modifier = Modifier.fillMaxWidth(0.90f),
          maxLines = 2,
          color = MaterialTheme.colorScheme.onSurface,
          overflow = TextOverflow.Ellipsis,
          style = TextStyle(
            fontSize = 16.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFFFFFFFF),
          )
        )
        if (task.isComplete) {
          Text(
            text = stringResource(Res.string.completed) + " ${task.completeTime}",
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.Start),
            style = TextStyle(
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 12.sp,
            )
          )
        }
      }
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(18.dp)

      ) {
        Icon(
          imageVector = Icons.Filled.CheckCircle,
          contentDescription = "Completed Task",
          modifier = Modifier.fillMaxSize() .padding(end = 18.dp),
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
      /*Icon(
        imageVector = vectorResource(Res.drawable.ic_check_circle),
        contentDescription = stringResource(Res.string.completed),
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
          .align(Alignment.CenterVertically)
          .padding(end = 18.dp)
      )*/
    }

    TaskItemBadges(
      task = task,
      modifier = Modifier.align(Alignment.BottomEnd)
    )
  }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//fun CompletePriorityItemPreview() {
//    MoreStuffTheme {
//        CompletePriorityItem(
//            task = TaskUiModel(
//                id = 0,
//                title = "Buy milk & bread & cheese & wine & chocolate & ice-cream & something mmm",
//                createTime = "",
//                completeTime = "",
//                isComplete = true,
//                priorityScore = 100,
//                position = 1,
//                extraDetails = true,
//                hasSchedule = true,
//                hasReminder = true,
//            ),
//            onClick = {}
//        )
//    }
//}