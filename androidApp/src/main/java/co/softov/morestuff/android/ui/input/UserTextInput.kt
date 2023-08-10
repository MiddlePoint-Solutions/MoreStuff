package co.softov.morestuff.android.ui.input

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.extension.clearFocusOnKeyboardDismiss
import co.softov.morestuff.android.ui.components.SendIcon
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun UserTextInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    sendAction: (String) -> Unit = {},
    actionsContent: @Composable () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.background,
) {

    val a11ylabel = stringResource(id = R.string.textfield_desc)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 15.dp ,end = 15.dp, bottom = 13.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(topStart = 47.dp, topEnd = 47.dp, bottomEnd = 47.dp, bottomStart = 47.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = a11ylabel
                },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 46.dp)
                    .align(Alignment.CenterVertically)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = true,
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss()
                        .weight(0.88f)
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions { sendAction(value.text) },
                    maxLines = 4,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(
                        color = LocalContentColor.current, fontSize = 18.sp
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (value.text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.write_something),
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .weight(0.12f)
                        .height(IntrinsicSize.Min)
                ) {
                    when {
                        value.text.isBlank() -> {
                            actionsContent()
                        }

                        else -> {
                            SendIcon(
                                onClick = { sendAction(value.text) }
                            )
                        }
                    }
                }
            }
        }
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
private fun Preview() {
    MoreStuffTheme {
        UserTextInput(
            value = TextFieldValue(text = ""),
            onValueChange = {},
        )
    }
}
