package co.softov.morestuff.android.ui.input

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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
import co.softov.morestuff.android.app.presentation.compose.modifier.clearFocusOnKeyboardDismiss
import co.softov.morestuff.android.ui.home.input.ListIcon
import co.softov.morestuff.android.ui.home.input.SendIcon
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import timber.log.Timber

@Composable
fun UserTextInput(
    initialValue: String = "",
    sendAction: (String) -> Unit,
    listAction: () -> Unit,
) {

    var value by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = initialValue))
    }

    LaunchedEffect(initialValue) {
        Timber.d("InitialValue: $initialValue")
        Timber.d("InitialValue: $value")
    }

    val a11ylabel = stringResource(id = R.string.textfield_desc)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
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
                    .weight(0.88f)
                    .align(Alignment.CenterVertically)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { value = it },
                    enabled = true,
                    modifier = Modifier
                        .clearFocusOnKeyboardDismiss()
                        .fillMaxWidth()
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
            }

            VoiceToTextInput(
                onUpdateValue = {
                    value = value.copy(text = it)
                }
            )

            Box(
                modifier = Modifier
                    .weight(0.12f)
                    .height(IntrinsicSize.Min)
            ) {
                when {
                    value.text.isBlank() -> ListIcon(listAction)
                    else -> SendIcon {
                        sendAction(value.text)
                        value = value.copy(text = "")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChatInputPreviewDark() {
    MoreStuffTheme() {
        UserTextInput(
            sendAction = {},
            listAction = {},
        )
    }
}

@Preview
@Composable
private fun ChatInputPreview() {
    MoreStuffTheme {
        UserTextInput(
            sendAction = {},
            listAction = {},
        )
    }
}
