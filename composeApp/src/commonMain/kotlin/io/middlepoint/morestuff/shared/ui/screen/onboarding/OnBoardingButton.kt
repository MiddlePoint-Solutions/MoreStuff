package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.theme.onBoardingButton

@Composable
fun OnBoardingButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  content: @Composable RowScope.() -> Unit
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth(0.8f)
      .height(56.dp),
    enabled = enabled,
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = Color.Companion.White,
      containerColor = onBoardingButton,
      disabledContentColor = Color.Companion.White.copy(alpha = 0.5f),
      disabledContainerColor = onBoardingButton.copy(alpha = 0.5f)
    ),
    border = null,

    content = content
  )
}