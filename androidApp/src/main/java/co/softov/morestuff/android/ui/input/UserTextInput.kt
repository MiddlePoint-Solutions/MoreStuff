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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
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


val LocalBoxWeight = compositionLocalOf { 0.12f }

@Composable
fun UserTextInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    sendAction: (String) -> Unit = {},
    actionsContent: @Composable () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {

    val a11ylabel = stringResource(id = R.string.textfield_desc)
    val boxWeight = LocalBoxWeight.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 6.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(42),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 42.dp)
                .semantics {
                    contentDescription = a11ylabel
                },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .clearFocusOnKeyboardDismiss()
                    .focusRequester(focusRequester)
                    .weight(0.88f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 28.dp, end = 4.dp),
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
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.padding(bottom = 6.dp, top = 6.dp)
                    ) {
                        if (value.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.main_input_hint),
                                modifier = Modifier.align(Alignment.CenterStart),
                                style = LocalTextStyle.current.copy(
                                    color = LocalContentColor.current.copy(alpha = 0.6f),
                                    fontSize = 18.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Box(
                modifier = Modifier
                    .weight(boxWeight)
                    .align(Alignment.Bottom)
                    .padding(end = 8.dp)
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
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
