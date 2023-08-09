package co.softov.morestuff.android.ui.chat.task

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.compose.keyboardAsState
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun TaskChatEditor(
    isComplete: Boolean,
    taskTitle: () -> String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    toggleTaskComplete: () -> Unit = {},
    taskOptions: @Composable ColumnScope.() -> Unit = {},
) {
    var isEditing by remember { mutableStateOf(false) }
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isKeyboardOpen) {
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
            isEditing = false
        }
    }

    BackHandler(isEditing) {
        focusManager.clearFocus()
        isEditing = false
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(
            modifier = Modifier
                .animateContentSize(animationSpec = tween())
                .padding(vertical = 2.dp)
                .then(
                    if (isEditing) {
                        Modifier
                            .fillMaxHeight(0.7f)
                    } else {
                        Modifier.height(IntrinsicSize.Min)
                    }
                ),
        ) {

            val toggleModifier by rememberUpdatedState(
                Modifier.then(
                    if (isEditing) {
                        Modifier.alpha(0.5f)
                    } else {
                        Modifier
                            .alpha(1f)
                            .clickable(onClick = toggleTaskComplete)
                    }
                )
            )

            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {

                Row {

                    Box(
                        Modifier
                            .padding(start = 12.dp, top = 4.dp, end = 18.dp)
                            .size(32.dp)
                    ) {
                        AnimatedContent(
                            targetState = isComplete,
                            label = "Complete toggle animation",
                            transitionSpec = { scaleIn() togetherWith fadeOut() },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            when (it) {
                                true -> {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = stringResource(R.string.cd_schedule_icon),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .then(toggleModifier)
                                    )
                                }

                                false -> {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = stringResource(R.string.cd_schedule_icon),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .then(toggleModifier)
                                    )
                                }
                            }
                        }
                    }

                    BasicTextField(
                        value = taskTitle(),
                        onValueChange = onTitleChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isEditing = focusState.isFocused
                            },
                        enabled = !isComplete,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrect = false,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions {
                            focusManager.clearFocus()
                        },
                        minLines = 2,
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            textDecoration = when (isComplete) {
                                true -> TextDecoration.LineThrough
                                false -> null
                            }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = !isComplete && !isEditing,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Column(content = taskOptions)
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
fun TaskChatTopBarPreview() {
    MoreStuffTheme {
        TaskChatEditor(
            isComplete = false,
            taskTitle = { "Hello There this should be a very long text so that we can test how it looks" },
            onTitleChange = {}
        )
    }
}