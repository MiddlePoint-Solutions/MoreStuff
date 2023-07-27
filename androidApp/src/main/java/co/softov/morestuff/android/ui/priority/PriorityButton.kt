package co.softov.morestuff.android.ui.priority

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
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
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.Orange600
import co.softov.morestuff.android.ui.theme.ProvideAppTheme

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
    PriorityButton(
        onClick = onClick,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit
) {

    val backgroundColor by animateColorAsState(
        targetValue = when (selected) {
            true -> Orange600
            else -> MaterialTheme.colorScheme.secondaryContainer
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
    ProvideAppTheme(theme = AppTheme.System) {
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
}
