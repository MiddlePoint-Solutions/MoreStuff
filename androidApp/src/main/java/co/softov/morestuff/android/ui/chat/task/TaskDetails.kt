package co.softov.morestuff.android.ui.chat.task

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.compose.keyboardAsState
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@Composable
fun TaskDetails(
    taskId: Long,
    modifier: Modifier = Modifier,
    taskOptions: @Composable ColumnScope.() -> Unit = {},
    viewModel: TaskDetailsViewModel = koinViewModel(
        key = "TaskChat$taskId",
        parameters = { parametersOf(taskId) }
    )
) {

    val task by viewModel.task.collectAsStateWithLifecycle()
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current
    var isEditing by remember { mutableStateOf(false) }
    var showSchedule by remember { mutableStateOf(false) }

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

    Box {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .then(
                        if (isEditing) {
                            Modifier.fillMaxHeight(0.7f)
                        } else {
                            Modifier.wrapContentHeight()
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
                                .clickable(onClick = viewModel::toggleTaskComplete)
                        }
                    )
                )

                Column(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 22.dp),
                    ) {
                        Box(
                            Modifier
                                .padding(start = 12.dp, end = 12.dp)
                                .size(32.dp)
                        ) {
                            AnimatedContent(
                                targetState = task.isComplete,
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


                        var firstTime by remember { mutableStateOf(true) }
                        var showEllipsis by remember { mutableStateOf(false) }
                        var lineEnd by remember { mutableIntStateOf(0) }
                        val uneditedText by remember(viewModel.taskTitle, showEllipsis) {
                            derivedStateOf {
                                if (showEllipsis) {
                                    viewModel.taskTitle.substring(0, lineEnd - 3) + "..."
                                } else {
                                    viewModel.taskTitle
                                }
                            }
                        }

                        BasicTextField(
                            value = if (isEditing || showSchedule) viewModel.taskTitle else uneditedText,
                            onValueChange = viewModel::updateTaskTitle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged {
                                    isEditing = it.isFocused
                                    if (it.isFocused) {
                                        showSchedule = false
                                    }
                                }
                                .padding(end = 20.dp),
                            enabled = !task.isComplete,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                autoCorrect = false,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions {
                                focusManager.clearFocus()
                            },
                            onTextLayout = {
                                if (isEditing || (firstTime && viewModel.taskTitle.isNotEmpty())) {
                                    firstTime = false
                                    showEllipsis = it.lineCount > 1
                                    lineEnd = it.getLineEnd(0, visibleEnd = true)
                                }
                            },
                            maxLines = if (isEditing || showSchedule) 4 else 1,
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        )
                    }

                    AnimatedVisibility(
                        visible = !task.isComplete && !isEditing,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Column(content = taskOptions)
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            AnimatedVisibility(visible = showSchedule) {
                                TaskSchedule(
                                    model = viewModel.scheduleModel,
                                    actionText = stringResource(id = R.string.task_chat_schedule_action),
                                    onTimeChange = viewModel::updatePlanTime,
                                    onDateChange = viewModel::updatePlanDate,
                                    createSchedule = viewModel::createOneTimeSchedule,
                                    cancelSchedule = viewModel::cancelActiveSchedule,
                                    icon = {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                                            contentDescription = stringResource(R.string.cd_schedule_icon),
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isEditing && !task.isComplete,
            modifier = Modifier
                .padding(end = 12.dp)
                .offset(y = 22.dp)
                .align(Alignment.BottomEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (task.hasSchedule && !showSchedule) {
                    Surface(
                        modifier = Modifier.size(width = 35.dp, height = 35.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape,
                    ) {
                        IconButton(onClick = { showSchedule = !showSchedule }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_schedule),
                                contentDescription = stringResource(R.string.cd_schedule_icon),
                            )
                        }
                    }
                }

                FilledIconButton(
                    onClick = { showSchedule = !showSchedule },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (showSchedule) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showSchedule) "Hide Schedule" else "Show Schedule"
                    )
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
fun TaskChatTopBarPreview() {
    MoreStuffTheme {
        TaskDetails(taskId = 1)
    }
}
