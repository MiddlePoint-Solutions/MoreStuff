package co.softov.morestuff.android.ui.scopes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.ui.components.CreateScopeButton
import co.softov.morestuff.android.ui.components.SendIcon
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.scopes.ScopesUiEvent.*
import co.softov.morestuff.android.ui.theme.md_theme_light_error
import co.softov.morestuff.android.ui.theme.surfaceContainer
import co.softov.morestuff.android.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopesScreen(
    onBack: () -> Unit
) {

    val viewModel: ScopesViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    var selectScopeId by remember { mutableLongStateOf(0) }
    var updateScopeName by remember { mutableStateOf("") }
    var isUserInputActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = (stringResource(R.string.title_scopes))) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {

            val (content, action) = createRefs()

            Column(
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(parent.top)
                    bottom.linkTo(action.top)
                    height = Dimension.fillToConstraints
                },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Icon(
                    imageVector = Icons.Filled.ModeStandby,
                    contentDescription = stringResource(R.string.cd_scopes_icon),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(60.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = stringResource(R.string.scopes_description),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Scopes(
                    scopes = viewModel.scopesState,
                    onEditScope = { scopeId, scopeName ->
                        selectScopeId = scopeId
                        updateScopeName = scopeName
                        isEditDialogOpen = true
                    },
                    onDeleteScope = { scopeId ->
                        selectScopeId = scopeId
                        isDeleteDialogOpen = true
                    },
                    handleUiEvent = viewModel::handleEvent
                )
            }

            Box(
                modifier = Modifier
                    .constrainAs(action) { bottom.linkTo(parent.bottom) }
                    .padding(vertical = 16.dp)
            ) {

                CreateScopeButton(
                    onClick = {
                        // TODO: show dialog
                        isUserInputActive = true
                        coroutineScope.launch {
                            delay(100)
                        }
                    }
                )

                when (isUserInputActive) {
                    true -> {
//                    BackHandler {
//                        isUserInputActive = false
//                    }
//                    ScopeInputComponent(
//                        focusRequester = focusRequester,
//                        handleUiEvent = viewModel::handleEvent,
//                        modifier = Modifier.fillMaxWidth()
//                    )
                    }

                    false -> {

                    }
                }
            }
        }



        if (isEditDialogOpen) {
            EditScopeDialog(
                onDismissRequest = { isEditDialogOpen = false },
                scopeName = updateScopeName,
                onConfirm = { newName ->
                    viewModel.handleEvent(UpdateScopeName(selectScopeId, newName))
                    isEditDialogOpen = false
                }
            )
        }

        if (isDeleteDialogOpen) {
            DeleteScopeDialog(
                onDismissRequest = { isDeleteDialogOpen = false },
                onConfirm = {
                    viewModel.handleEvent(DeleteScope(selectScopeId))
                    isDeleteDialogOpen = false
                },
            )
        }

    }
}

@Composable
private fun Scopes(
    scopes: List<ScopeDomain>,
    onEditScope: (Long, String) -> Unit,
    onDeleteScope: (Long) -> Unit,
    handleUiEvent: (ScopesUiEvent) -> Unit,
) {
    val menuVisibility = remember(scopes) {
        mutableStateMapOf<Long, Boolean>().apply {
            scopes.forEach { scope ->
                put(scope.id, false)
            }
        }
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to -> handleUiEvent(ReorderScope(from.index, to.index, false)) },
        onDragEnd = { from, to -> handleUiEvent(ReorderScope(from, to, true)) }
    )

    Column(
        modifier = Modifier.padding(top = 20.dp)
    ) {

        Text(
            text = stringResource(R.string.title_scopes),
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        LazyColumn(
            state = reorderState.listState,
            modifier = Modifier
                .fillMaxSize()
                .reorderable(reorderState)
                .detectReorderAfterLongPress(reorderState)
        ) {
            items(
                items = scopes,
                key = { scope -> scope.id }
            ) { scope ->

                ReorderableItem(
                    state = reorderState,
                    key = scope.id
                ) {

                    Column(
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.surfaceContainerElevation
                        )
                    ) {
                        ListItem(
                            modifier = Modifier.fillMaxWidth(),
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    modifier = Modifier.detectReorder(reorderState),
                                    contentDescription = stringResource(R.string.cd_move_icon),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            headlineContent = {
                                Text(text = scope.name)
                            },
                            trailingContent = {
                                if (scope.id != 1L) {
                                    IconButton(onClick = {
                                        menuVisibility[scope.id] = !menuVisibility[scope.id]!!
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = stringResource(R.string.cd_dots_menu),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = menuVisibility[scope.id] == true,
                                    onDismissRequest = { menuVisibility[scope.id] = false }
                                ) {
                                    DropdownMenuItem(onClick = {
                                        onEditScope(scope.id, scope.name)
                                        menuVisibility[scope.id] = false
                                    },
                                        text = { Text(text = stringResource(R.string.edit_scope_name)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit"
                                            )
                                        }
                                    )
                                    DropdownMenuItem(onClick = {
                                        onDeleteScope(scope.id)
                                        menuVisibility[scope.id] = false
                                    },
                                        text = { Text(text = stringResource(R.string.delete)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Delete",
                                                tint = md_theme_light_error
                                            )
                                        }
                                    )

                                }
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
                            )
                        )

                        Divider(
                            modifier = Modifier.padding(start = 45.dp),
                            thickness = Dp.Hairline
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun EditScopeDialog(
    onDismissRequest: () -> Unit,
    scopeName: String,
    onConfirm: (String) -> Unit,
) {

    var dialogText by remember { mutableStateOf(scopeName) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.edit_scope_name)) },
        text = {
            TextField(
                value = dialogText,
                onValueChange = { dialogText = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
        },

        confirmButton = {
            Button(
                onClick = { onConfirm(dialogText) },
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ScopeInputComponent(
    focusRequester: FocusRequester,
    handleUiEvent: (ScopesUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var newScopeName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val isTextEmpty = remember(newScopeName.text) {
        mutableStateOf(newScopeName.text.isBlank())
    }

    Box {
        UserInput(
            textContent = {
                UserTextInput(
                    value = newScopeName,
                    onValueChange = { newScopeName = it },
                    focusRequester = focusRequester,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    actionsContent = {
                        if (isTextEmpty.value) {
                            VoiceToTextInput(
                                onUpdateValue = {
                                    newScopeName = newScopeName.copy("")
                                }
                            )
                        } else {
                            AnimatedVisibility(
                                visible = !isTextEmpty.value,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                SendIcon(onClick = {
                                    handleUiEvent(CreateScope(newScopeName.text))
                                    newScopeName = newScopeName.copy("")
                                })
                            }
                        }
                    },

                    )
            })
    }

}

@Composable
private fun DeleteScopeDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.confirm_delete)) },
        text = {
            Text(
                text = stringResource(R.string.sure_delete_scope),
                textAlign = TextAlign.Start
            )
        },

        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                },
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
