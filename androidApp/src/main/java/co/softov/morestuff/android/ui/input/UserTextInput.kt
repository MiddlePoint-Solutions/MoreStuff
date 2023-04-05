package co.softov.morestuff.android.ui.input

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import co.softov.morestuff.android.VoiceToTextParserState
import co.softov.morestuff.android.app.presentation.compose.modifier.clearFocusOnKeyboardDismiss
import co.softov.morestuff.android.ui.main.input.ListIcon
import co.softov.morestuff.android.ui.main.input.SendIcon
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserTextInput(
    sendAction: (String) -> Unit,
    listAction: () -> Unit,
    canRecord: Boolean,
    state: VoiceToTextParserState,
    onRecordClick: () -> Unit,
) {

    var value by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(state.spokenText))
    }
    LaunchedEffect(state.spokenText) {
        value = TextFieldValue(state.spokenText)
    }

    val a11ylabel = stringResource(id = R.string.textfield_desc)

    Surface(
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.primary)
                .semantics {
                    contentDescription = a11ylabel
                }, horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom
        ) {
            Row(
                modifier = Modifier.defaultMinSize(minHeight = 46.dp).weight(0.88f)
                    .align(Alignment.CenterVertically)
            ) {
                BasicTextField(value = value,
                    onValueChange = { value = it },
                    enabled = true,
                    modifier = Modifier.clearFocusOnKeyboardDismiss().fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp)
                        .onFocusChanged {
                            Timber.d("FocusState changed: $it")
                        },
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
                    })
            }

            Box(
                modifier = Modifier.weight(0.12f).height(IntrinsicSize.Min)
            ) {
                if (canRecord) {
                    IconButton(onClick = onRecordClick) {
                        AnimatedContent(targetState = state.isSpeaking) { isSpeaking ->
                            if (isSpeaking) {
                                Icon(
                                    imageVector = Icons.Filled.Stop,
                                    contentDescription = "",
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "",
                                )
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier.weight(0.12f).height(IntrinsicSize.Min)
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
    MoreStuffTheme(darkTheme = true) {
        UserTextInput(
            sendAction = {},
            listAction = {},
            canRecord = true,
            state = VoiceToTextParserState(),
            onRecordClick = {}

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
            canRecord = true,
            state = VoiceToTextParserState(),
            onRecordClick = {}
        )
    }
}
