package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppChatItemLoading() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(end = 45.dp),
    horizontalArrangement = Arrangement.Start
  ) {
    Box(
      modifier = Modifier
        .padding(start = 5.dp, bottom = 4.dp)
    ) {
      Surface(
        onClick = { },
        shape = RoundedCornerShape(
          topStart = 14.dp,
          topEnd = 14.dp,
          bottomEnd = 14.dp,
          bottomStart = 5.dp
        ),
        color = MaterialTheme.colorScheme.surfaceVariant,
      ) {
        Column {
          TypingDots(
            modifier = Modifier
              .padding(
                start = 18.dp,
                end = 18.dp,
                top = 12.dp,
                bottom = 12.dp
              )
          )
        }
      }
    }
  }
}


@Composable
fun TypingDots(
  modifier: Modifier = Modifier,
  dotColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
  dotSize: Dp = 10.dp,
  dotCount: Int = 3,
  delayMillis: Int = 300,
  animationDuration: Int = 600
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(dotSize / 2)
  ) {
    repeat(dotCount) { index ->
      val infiniteTransition = rememberInfiniteTransition(label = "dotTransition$index")

      val totalDuration = animationDuration + (delayMillis * (dotCount - 1))

      val scale by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
          animation = keyframes {
            durationMillis = totalDuration
            0.6f at 0
            1.2f at index * delayMillis using FastOutSlowInEasing
            1.2f at index * delayMillis + (animationDuration / 2)
            0.6f at index * delayMillis + animationDuration
          },
          repeatMode = RepeatMode.Restart
        ),
        label = "scale$index"
      )

      val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
          animation = keyframes {
            durationMillis = totalDuration
            0.3f at 0
            0.9f at index * delayMillis using FastOutSlowInEasing
            0.9f at index * delayMillis + (animationDuration / 2)
            0.3f at index * delayMillis + animationDuration
          },
          repeatMode = RepeatMode.Restart
        ),
        label = "alpha$index"
      )

      Box(
        modifier = Modifier
          .size(dotSize)
          .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
          }
          .background(
            color = dotColor,
            shape = CircleShape
          )
      )
    }
  }
}