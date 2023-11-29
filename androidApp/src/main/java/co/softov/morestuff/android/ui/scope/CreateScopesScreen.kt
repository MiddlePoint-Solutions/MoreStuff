package co.softov.morestuff.android.ui.scope

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.components.SendIcon
import co.softov.morestuff.android.ui.home.HomeViewModel
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScopesScreen(onBack: () -> Unit) {
    val scopeViewModel: ScopeViewModel = koinViewModel()
    val homeViewModel: HomeViewModel = koinViewModel()
    val scopes = scopeViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isEditDialogOpen = remember { mutableStateOf(false) }
    val isDeleteDialogOpen = remember { mutableStateOf(false) }
    var selectScopeId by remember { mutableStateOf<Long?>(null) }
    var updateScopeName by remember { mutableStateOf(TextFieldValue()) }
    var isUserInputActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = (stringResource(R.string.create_scope))) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_back)
                        )
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
            ManageScopeList(
                scopes = scopes,
                onScopeSelected = { scopeId ->
                    scopeViewModel.handleEvent(ScopeUiEvent.SelectScope(scopeId))
                    homeViewModel.addSelectedTasksToScope(scopeId)
                    homeViewModel.clearSelectedTasks()
                },
                hideSheet = {},
                onEditScope = { scopeId, scopeName ->
                    selectScopeId = scopeId
                    updateScopeName = TextFieldValue(scopeName)
                    isEditDialogOpen.value = true
                },
                onDeleteScope = { scopeId ->
                    selectScopeId = scopeId
                    isDeleteDialogOpen.value = true
                }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (isUserInputActive) {
                BackHandler {
                    isUserInputActive = false
                }
                UserInputComponent(
                    focusRequester = focusRequester,
                    scopeViewModel = scopeViewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (!isUserInputActive) {
                CreateScopeButton(
                    isUserInputActive = isUserInputActive,
                    onButtonClick = {
                        isUserInputActive = true
                        coroutineScope.launch {
                            delay(100)
                            focusRequester.requestFocus()
                        }
                    }
                )
            }
        }

        EditScopeDialog(
            isEditDialogOpen = isEditDialogOpen,
            scopeId = selectScopeId,
            scopeName = remember { mutableStateOf(updateScopeName) },
            onConfirm = { scopeId, newName ->
                coroutineScope.launch {
                    scopeViewModel.handleEvent(
                        ScopeUiEvent.UpdateScopeName(scopeId, newName)
                    )
                }
                isEditDialogOpen.value = false
            },
            onDismiss = {
                isEditDialogOpen.value = false
            }
        )
        DeleteDialog(
            isDeleteDialogOpen = isDeleteDialogOpen,
            scopeId = selectScopeId,
            onConfirm = { scopeId ->
                scopeViewModel.deleteScopes(listOf(scopeId))
                isDeleteDialogOpen.value = false
            },
            onDismiss = {
                isDeleteDialogOpen.value = false
            }
        )
    }
}


@Composable
fun ManageScopeList(
    scopes: State<ScopeUiModel>,
    onScopeSelected: (Long) -> Unit,
    onEditScope: (Long, String) -> Unit,
    onDeleteScope: (Long) -> Unit,
    hideSheet: () -> Unit,
) {
    val scrollState = rememberLazyListState()

    Text(
        text = stringResource(R.string.task_scopes),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp,
        fontWeight = FontWeight(400),
        modifier = Modifier.padding(start = 10.dp)
    )
    LazyColumn(
        state = scrollState
    ) {
        items(scopes.value.scopes.size) { index ->
            val scope = scopes.value.scopes[index]
            ListItem(
                modifier = Modifier.clickable {
                    onScopeSelected(scope.scopeId)
                    hideSheet()
                },
                headlineContent = {
                    Text(scope.name)
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (scope.scopeId != 1L) {
                            IconButton(onClick = { onEditScope(scope.scopeId, scope.name) }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                            Spacer(Modifier.width(10.dp))

                            IconButton(onClick = { onDeleteScope(scope.scopeId) }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFFFB0CF)
                                )
                            }
                        }
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                )
            )
            if (index < scopes.value.scopes.size - 1) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = Dp.Hairline
                )
            }
        }
    }
}


@Composable
fun EditScopeDialog(
    isEditDialogOpen: MutableState<Boolean>,
    scopeId: Long?,
    scopeName: MutableState<TextFieldValue>,
    onConfirm: (Long, String) -> Unit,
    onDismiss: () -> Unit,
) {
    if (isEditDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { isEditDialogOpen.value = false },
            title = { Text(text = stringResource(R.string.edit_scope_name)) },
            text = {
                TextField(
                    value = scopeName.value,
                    onValueChange = { newValue -> scopeName.value = newValue },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        scopeId?.let { id ->
                            onConfirm(id, scopeName.value.text)
                            isEditDialogOpen.value = false
                        }
                    },
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isEditDialogOpen.value = false
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun UserInputComponent(
    focusRequester: FocusRequester,
    scopeViewModel: ScopeViewModel,
    modifier: Modifier,
) {
    var newScopeName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val isTextEmpty = remember(newScopeName.text) {
        mutableStateOf(newScopeName.text.isBlank())
    }
    val coroutineScope = rememberCoroutineScope()
    Box {
        UserInput(
            backgroundColor = Color.Transparent,
            textContent = {
                UserTextInput(
                    value = newScopeName,
                    onValueChange = { newScopeName = it },
                    focusRequester = focusRequester,
                    sendAction = {
                        coroutineScope.launch {
                            scopeViewModel.handleEvent(
                                ScopeUiEvent.CreateScope(
                                    UUID.randomUUID().toString(),
                                    newScopeName.text
                                )
                            )
                            newScopeName = newScopeName.copy("")
                        }
                    },
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
                                    coroutineScope.launch {
                                        scopeViewModel.handleEvent(
                                            ScopeUiEvent.CreateScope(
                                                UUID.randomUUID().toString(),
                                                newScopeName.text
                                            )
                                        )
                                        newScopeName = newScopeName.copy("")
                                    }
                                })
                            }
                        }
                    },

                    )
            })
    }

}

@Composable
fun CreateScopeButton(
    isUserInputActive: Boolean = true,
    onButtonClick: () -> Unit,
) {
    if (!isUserInputActive) {
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                painter = painterResource(R.drawable.add_circle),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                contentDescription = "Create",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.create_new_scope))
        }
    }
}

@Composable
fun DeleteDialog(
    isDeleteDialogOpen: MutableState<Boolean>,
    scopeId: Long?,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    if (isDeleteDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { isDeleteDialogOpen.value = false },
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
                        scopeId?.let { id ->
                            onConfirm(id)
                            isDeleteDialogOpen.value = false
                        }
                    },
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isDeleteDialogOpen.value = false
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
