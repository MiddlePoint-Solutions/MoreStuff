package co.softov.morestuff.android.ui.scopes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.nav.ScopeScreen
import co.softov.morestuff.android.domain.nav.ScopeScreen.*
import co.softov.morestuff.android.ui.components.CreateScopeButton
import co.softov.morestuff.android.ui.navigation.ChildStack
import co.softov.morestuff.android.ui.scopes.ScopesUiEvent.*
import co.softov.morestuff.android.ui.theme.md_theme_light_error
import co.softov.morestuff.android.ui.theme.surfaceContainerElevation
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScopesScreen(
    onBack: () -> Unit,
    initialScreen: ScopeScreen = Root,
    viewModel: ScopesViewModel = koinViewModel()
) {

    val navigation = remember { StackNavigation<ScopeScreen>() }

    ChildStack(
        source = navigation,
        initialStack = { listOf(initialScreen) },
        modifier = Modifier.background(Color.Transparent),
        key = "ScopesStack",
        handleBackButton = true,
        animation = stackAnimation(slide()),
    ) { screen ->
        when (screen) {
            Root -> ScopesContent(
                onBack = onBack,
                onCreateScope = { navigation.push(Create) },
                onEvent = viewModel::handleEvent
            )

            Create -> CreateScopeScreen(
                onBack = navigation::pop,
                onSaveScope = { title ->
                    viewModel.handleEvent(CreateScope(title))
                    navigation.pop()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopesContent(
    onBack: () -> Unit,
    onCreateScope: () -> Unit,
    onEvent: (ScopesUiEvent) -> Unit,
) {

    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    var selectedScope by remember { mutableStateOf<ScopeDomain?>(null) }

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
                    text = stringResource(R.string.description_scopes),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                OrderedScopesList(
                    onEditScope = { scope ->
                        selectedScope = scope
                        isEditDialogOpen = true
                    },
                    onDeleteScope = { scopeId ->
                        selectedScope = scopeId
                        isDeleteDialogOpen = true
                    }
                )
            }

            Box(
                modifier = Modifier
                    .constrainAs(action) { bottom.linkTo(parent.bottom) }
                    .padding(vertical = 16.dp)
            ) {
                CreateScopeButton(onClick = onCreateScope)
            }
        }

        if (isEditDialogOpen) {
            val scope = selectedScope ?: error("Scope is null")
            EditScopeDialog(
                onDismissRequest = { isEditDialogOpen = false },
                scopeName = scope.name,
                onConfirm = { newName ->
                    onEvent(UpdateScopeName(scope.id, newName))
                    isEditDialogOpen = false
                }
            )
        }

        if (isDeleteDialogOpen) {
            val scope = selectedScope ?: error("Scope is null")
            DeleteScopeDialog(
                onDismissRequest = { isDeleteDialogOpen = false },
                scopeName = scope.name,
                onConfirm = {
                    onEvent(DeleteScope(scope.id))
                    isDeleteDialogOpen = false
                },
            )
        }

    }
}

@Composable
private fun OrderedScopesList(
    onEditScope: (ScopeDomain) -> Unit,
    onDeleteScope: (ScopeDomain) -> Unit,
    viewModel: ScopesViewModel = koinViewModel()
) {

    val menuVisibility = remember(viewModel.scopesState) {
        mutableStateMapOf<Long, Boolean>().apply {
            viewModel.scopesState.forEach { scope ->
                put(scope.id, false)
            }
        }
    }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to -> viewModel.handleEvent(ReorderScope(from.index, to.index, false)) },
        onDragEnd = { from, to -> viewModel.handleEvent(ReorderScope(from, to, true)) }
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
                items = viewModel.scopesState,
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
                                if (scope.id != defaultScope.id) {
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
                                        onEditScope(scope)
                                        menuVisibility[scope.id] = false
                                    },
                                        text = { Text(text = stringResource(R.string.edit_scope)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit"
                                            )
                                        }
                                    )
                                    DropdownMenuItem(onClick = {
                                        onDeleteScope(scope)
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
        title = { Text(text = stringResource(R.string.edit_scope)) },
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
                enabled = dialogText.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
    )
}

@Composable
private fun DeleteScopeDialog(
    onDismissRequest: () -> Unit,
    scopeName: String,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.delete_scope)) },
        text = {
            Text(
                text = stringResource(R.string.sure_delete_scope, scopeName),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
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
