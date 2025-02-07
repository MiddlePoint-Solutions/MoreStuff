package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.TaskColors
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.Messages
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_extra_details
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskCard(
  item: ReviewItemUiModel,
  modifier: Modifier = Modifier,
  messages: List<MessageUiModel> = listOf()
) {

  val backgroundColors by remember {
    derivedStateOf { TaskColors.getProfileColorsForTask(item.title) }
  }

  Card(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .aspectRatio(0.7f),
    elevation = CardDefaults.cardElevation(
      defaultElevation = 8.dp
    ),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    border = BorderStroke(0.3.dp, color = Color.Black.copy(alpha = 0.18f))
  ) {
    Text(
      text = item.title,
      color = MaterialTheme.colorScheme.onSecondaryContainer,
      textAlign = TextAlign.Start,
      style = MaterialTheme.typography.displaySmall.copy(
        fontWeight = FontWeight(400),
        fontSize = 25.sp,
        lineHeight = 28.sp
      ),
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp, start = 20.dp, end = 20.dp),
      maxLines = 2,
      minLines = 2,
      overflow = TextOverflow.Ellipsis
    )

    Box(
      modifier = Modifier
        .aspectRatio(1f)
        .padding(start = 20.dp, end = 20.dp, top = 6.dp),
    ) {

      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 15.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(brush = Brush.verticalGradient(backgroundColors))
      ) {
        Messages(
          messages = messages,
          scrollState = rememberLazyListState().apply {  },
          contentPadding = PaddingValues(top = 40.dp, bottom = 20.dp, end = 10.dp),
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(
          space = 10.dp,
          alignment = Alignment.End
        )
      ) {

      }
    }
  }
}

@NonRestartableComposable
@Composable
private fun topScrimBrush() = Brush.verticalGradient(
  listOf(
    Color.White.copy(alpha = 0.1f),
    Color.White.copy(alpha = 0f)
  )
)

@NonRestartableComposable
@Composable
private fun bottomScrimBrush() = Brush.verticalGradient(
  listOf(
    Color.White.copy(alpha = 0f),
    Color.White.copy(alpha = 0.5f)
  )
)

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//fun TaskCardPreview() {
//    MoreStuffTheme() {
//        TaskCard(
//            modifier = Modifier
//                .fillMaxSize()
//                .aspectRatio(1f),
//            item = ReviewItemUiModel(
//                title = "Hellooooo there",
//                createTime = "",
//                position = "1/10",
//                id = 0,
//                priorityScore = 0,
//                isCompleted = false,
//                extraDetails = true
//            ),
//            onComplete = {},
//            showTaskChat = {}
//        )
//    }
//}