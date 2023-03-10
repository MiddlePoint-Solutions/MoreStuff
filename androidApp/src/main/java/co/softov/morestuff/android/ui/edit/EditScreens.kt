package co.softov.morestuff.android.ui.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.presentation.model.PriorityModel
import co.softov.morestuff.android.presentation.model.mapToModel
import co.softov.morestuff.android.presentation.presenter.PriorityOptionsPresenter
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.priority.PriorityInput
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskTitleBottomSheet(
    openBottomSheet: Boolean,
    bottomSheetState: SheetState = rememberSheetState(skipHalfExpanded = false),
    dismissAction: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    var priority: PriorityModel by remember { mutableStateOf(PriorityModel.Today(DefaultOption.Auto)) }

    val priorityOptions by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
        PriorityOptionsPresenter()
    }.collectAsState()

    var showConfirm by remember { mutableStateOf(true) }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = dismissAction,
            sheetState = bottomSheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    shape = MaterialTheme.shapes.small
                )
            }
        ) {

            AnimatedVisibility(
                visible = showConfirm,
                enter = expandVertically(
                    animationSpec = spring()
                ),
                exit = shrinkVertically(
                    animationSpec = spring()
                )
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false,
                ) {
                    PriorityButton(
                        onSelected = {
                            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    dismissAction()
                                }
                            }
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(),
                        text = "Confirm".uppercase(),
                        fontSize = 20.sp
                    )
                }
            }

            PriorityInput(
                priority = priority,
                onPriorityChange = { change ->
                    priority = change.mapToModel()
                    showConfirm = true
                },
                priorityOptions = priorityOptions,
                onPriorityOptionChange = { showConfirm = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskTitleAlertDialog(
    openBottomSheet: Boolean,
    dismissAction: () -> Unit,
) {

    val bottomSheetState = rememberSheetState(skipHalfExpanded = false)

    if (openBottomSheet) {
        AlertDialog(
            onDismissRequest = dismissAction,
        ) {
            EditTaskTitle(
                dismissAction = { /* Change task title */ },
                title = "Implement task title editing",
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTaskTitle(
    dismissAction: () -> Unit,
    title: String,
) {

    val kc = LocalSoftwareKeyboardController.current

    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(title, TextRange(0, title.length)))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val focusRequester = remember { FocusRequester() }

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    Timber.d("FocusState changed: $it")
                }
        )

        Row {
            IconButton(onClick = {
                kc?.hide()
                dismissAction()
            }) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Localized description"
                )
            }

            IconButton(onClick = dismissAction) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Localized description"
                )
            }
        }

        LaunchedEffect(key1 = Unit, block = {
            focusRequester.requestFocus()
        })

    }
}

@Preview
@Composable
fun EditTaskTitlePreview() {
    MoreStuffTheme(darkTheme = true) {
//        EditTaskTitleBottomSheet(true, {})
    }
}