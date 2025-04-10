package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.theme.TaskColors

@Composable
fun TaskProfile(
  title: String,
  modifier: Modifier = Modifier,
) {

  val profileColor by remember(title) {
    derivedStateOf { TaskColors.getProfileColorsForTask(title) }
  }

  val profileTitle by remember(title) {
    derivedStateOf { title.getOrNull(0)?.uppercase() ?: "" }
  }

  Box(
    modifier = modifier
      .clip(CircleShape)
      .size(50.dp)
      .background(
        brush = Brush.Companion.verticalGradient(profileColor)
      )
  ) {
    BasicText(
      text = profileTitle,
      modifier = Modifier.align(Alignment.Center),
      style = TextStyle(
        fontSize = 24.sp,
        lineHeight = 23.8.sp,
        fontWeight = FontWeight(400),
        color = Color.White,
      )
    )
  }
}