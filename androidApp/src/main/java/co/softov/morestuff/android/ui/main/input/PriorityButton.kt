package co.softov.morestuff.android.ui.main.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriorityButton(
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    selected: Boolean = false
) {

    val backgroundColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colors.secondaryVariant
            else -> MaterialTheme.colors.primary
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val textColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colors.onSecondary
            else -> MaterialTheme.colors.onPrimary
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    Button(
        modifier = modifier,
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = textColor
        ),
        onClick = onSelected
    ) {
        Text(text = text, softWrap = false, fontSize = 13.sp)
    }
}