package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun PriorityButton(
    onSelected: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    fontSize: TextUnit = 13.sp,
    shape: Shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
) {
    PriorityButton(
        onSelected = onSelected,
        modifier = modifier.layoutId(text),
        selected = selected,
        enabled = enabled,
        shape = shape,
    ) {
        Text(text = text, softWrap = false, fontSize = fontSize)
    }
}

@Composable
fun PriorityButton(
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
    content: @Composable () -> Unit
) {

    val backgroundColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "Background Color Animation"
    )

    val textColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colorScheme.onSecondary
            else -> MaterialTheme.colorScheme.onPrimary
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "Text Color Animation"
    )

    Button(
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        onClick = onSelected,
        enabled = enabled,
    ) {
        content()
    }
}

@Preview
@Composable
fun PriorityButtonPreview() {
    MoreStuffTheme() {

    }
}

