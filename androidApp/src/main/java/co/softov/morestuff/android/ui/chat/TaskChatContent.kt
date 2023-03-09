package co.softov.morestuff.android.ui.chat

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.util.anyLog
import co.softov.morestuff.android.domain.model.Task
import co.softov.morestuff.android.ui.compose.BackPressHandler
import co.softov.morestuff.android.ui.compose.LocalBackPressedDispatcher
import co.softov.morestuff.android.ui.edit.EditTaskTitleAlertDialog
import co.softov.morestuff.android.ui.edit.EditTaskTitleBottomSheet
import co.softov.morestuff.android.ui.priority.PriorityInput
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.google.accompanist.insets.ui.Scaffold
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

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

    val chatActions = ChatActions(
        scheduleAction = viewModel::scheduleResponse,
    )

    var openDialog by remember { mutableStateOf(false) }

    val messageItems by viewModel.messages.collectAsState()
    val task by viewModel.task.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier,
        topBar = {
            TaskChatTopBar(
                title = viewModel.taskTitle,
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

                    PriorityInput(modifier)
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TaskChatTopBar(
    title: String,
    backNavigationAction: () -> Unit,
    editTitleAction: (String) -> Unit,
) {

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
        val focusManager = LocalFocusManager.current
        Surface {
            BasicTextField(
                value = title,
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
            title = "Edit task title in task chat",
            backNavigationAction = { },
            editTitleAction = { },
        )
    }
}
