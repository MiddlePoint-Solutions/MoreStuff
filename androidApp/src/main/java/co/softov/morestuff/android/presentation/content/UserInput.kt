package co.softov.morestuff.android.presentation.content

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
fun UserTextInput(
    sendAction: (String) -> Unit,
    listAction: () -> Unit
) {

    var value by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    val a11ylabel = stringResource(id = R.string.textfield_desc)

    Surface(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = a11ylabel
                }
                .animateContentSize(),
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
                    modifier = Modifier
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
                                Text(text = "Write something...", fontSize = 18.sp)
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.12f)
                    .height(IntrinsicSize.Min)
            ) {
                when {
                    value.text.isBlank() -> ListIcon(listAction)
                    else -> SendIcon { sendAction(value.text) }
                }
            }
        }
    }
}

@Composable
private fun ListIcon(listIconClick: () -> Unit) {
    IconButton(
        onClick = listIconClick
    ) {
        Icon(Icons.Default.List, contentDescription = null)
    }
}

@Composable
private fun SendIcon(sendIconClick: () -> Unit) {
    IconButton(
        onClick = sendIconClick
    ) {
        Icon(Icons.Default.Send, contentDescription = null)
    }
}


@Preview
@Composable
private fun ChatInputPreviewDark() {
    MoreStuffTheme(darkTheme = true) {
        UserTextInput(
            sendAction = {},
            listAction = {}
        )
    }
}

@Preview
@Composable
private fun ChatInputPreview() {
    MoreStuffTheme {
        UserTextInput(
            sendAction = {},
            listAction = {}
        )
    }
}