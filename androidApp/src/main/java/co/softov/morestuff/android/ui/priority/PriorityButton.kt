package co.softov.morestuff.android.ui.priority

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun PriorityButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    fontSize: TextUnit = 13.sp,
    shape: Shape = CircleShape,
) {
    SetSchedulePriorityButton(
        onClick = onClick,
        modifier = modifier.layoutId(text),
        enabled = enabled,
        shape = shape,
    ) {
        Text(
            text = text,
            softWrap = false,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PriorityButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit,
) {

    val backgroundColor by animateColorAsState(
        targetValue = when (selected) {
            true -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12F)
            else -> Color.Transparent
        },
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "Background Color Animation"
    )

    val textColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onSecondary,
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
        onClick = onClick,
        enabled = enabled,
    ) {
        content()
    }
}

@Composable
fun SetSchedulePriorityButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit,
) {

    val backgroundColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary.copy(alpha = 0.12F),
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "Background Color Animation"
    )

    val textColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
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
        onClick = onClick,
        enabled = enabled,
    ) {
        content()
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
private fun PriorityButtonPreviewDark() {
    MoreStuffTheme {
        Column {
            PriorityButton(
                modifier = Modifier.width(100.dp),
                selected = true,
                onClick = {},
            ) {
                Text(
                    text = stringResource(id = R.string.priority_now),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }

            PriorityButton(
                modifier = Modifier.width(100.dp),
                selected = false,
                onClick = {},
            ) {
                Text(
                    text = stringResource(id = R.string.priority_later),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }

            PriorityButton(
                modifier = Modifier.width(100.dp),
                selected = false,
                onClick = {},
            ) {
                Text(
                    text = stringResource(id = R.string.priority_plan),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }
        }
    }
}
