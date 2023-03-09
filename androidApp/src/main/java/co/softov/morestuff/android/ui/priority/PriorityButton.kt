package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriorityButton(
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
) {

    val backgroundColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    val textColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colorScheme.onSecondary
            else -> MaterialTheme.colorScheme.onPrimary
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing)
    )

    TextButton(
        modifier = modifier.layoutId(text),
        shape =  shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        onClick = onSelected
    ) {
        Text(text = text, softWrap = false, fontSize = 13.sp)
    }
}