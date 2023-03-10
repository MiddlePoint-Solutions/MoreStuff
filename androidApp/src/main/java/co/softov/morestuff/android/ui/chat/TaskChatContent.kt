package co.softov.morestuff.android.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import co.softov.morestuff.android.R
import co.softov.morestuff.android.presentation.presenter.PriorityOptionsPresenter
import co.softov.morestuff.android.presentation.presenter.PriorityPresenter
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.ui.Scaffold
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskChatContent(
    taskId: Long,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM") {
        parametersOf(taskId)
    }

    val priority by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
        PriorityPresenter()
    }.collectAsState()

    val priorityOptions by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
        PriorityOptionsPresenter()
    }.collectAsState()

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
    )

    val messageItems by viewModel.messages.collectAsState()
    val task by viewModel.task.collectAsState()

    var showConfirm by remember { mutableStateOf(true) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TaskChatTopBar(
                titleProvider = { viewModel.taskTitle },
                backNavigationAction = viewModel::onBackPressed,
                editTitleAction = viewModel::updateTaskTitle,
            )
        },
        content = {
            Surface(modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    Messages(
                        messages = messageItems,
                        actions = chatActions,
                        modifier = modifier.weight(1f),
                        scrollState = scrollState
                    )

                    AnimatedVisibility(
                        visible = showConfirm,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentEnforcement provides false,
                        ) {
                            PriorityButton(
                                onSelected = { showConfirm = false },
                                modifier = Modifier.fillMaxWidth(),
                                text = "Confirm".uppercase(),
                            )
                        }
                    }

                    PriorityInput(priority, priorityOptions)

                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskChatTopBar(
    titleProvider: () -> String,
    backNavigationAction: () -> Unit,
    editTitleAction: (String) -> Unit,
) {

    val focusManager = LocalFocusManager.current

    Column {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { backNavigationAction() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigate_pack)
                    )
                }
            }
        )

        Surface {
            BasicTextField(
                value = titleProvider(),
                onValueChange = {
                    editTitleAction(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeContent),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = false,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                maxLines = 4,
                textStyle = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
            )
        }

    }
}

@Preview
@Composable
private fun TaskChatTopBarPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskChatTopBar(
            titleProvider = { "Edit task title in task chat" },
            backNavigationAction = { },
            editTitleAction = { },
        )
    }
}

@Preview
@Composable
private fun TaskChatPreview() {
    MoreStuffTheme(darkTheme = true) {
        TaskChatContent(
            taskId = 1
        )
    }
}
