package co.softov.morestuff.android.presentation.content

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.Priority
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
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.primary)
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
                    else -> SendIcon {
                        sendAction(value.text)
                        value = value.copy(text = "")
                    }
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

val Priority.title: String
    @Composable get() = when (this) {
        is Priority.Today -> stringResource(id = R.string.priority_today)
        is Priority.Tomorrow -> stringResource(id = R.string.priority_tomorrow)
        is Priority.Later -> stringResource(id = R.string.priority_later)
    }

@Composable
fun UserPriorityInput(
    modifier: Modifier = Modifier,
    currentPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {

    val priorityButtons = listOf(Priority.Today(), Priority.Tomorrow(), Priority.Later())

    Surface(
        elevation = 4.dp
    ) {
        Row {
            priorityButtons.forEach {
                PriorityButton(
                    modifier = Modifier
                        .weight(0.34f)
                        .height(50.dp),
                    selected = it == currentPriority,
                    onSelected = { onPrioritySelected(it) },
                    text = it.title.toUpperCase(Locale.current)
                )
            }
        }
    }
}

@Composable
private fun PriorityButton(
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

@Preview
@Composable
fun UserPriorityInputPreviewDark() {
    MoreStuffTheme(darkTheme = true) {
        UserPriorityInput(
            currentPriority = Priority.Today()
        ) {

        }
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