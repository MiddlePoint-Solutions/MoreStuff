package io.middlepoint.morestuff.shared.ui.screen.scopes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ModeStandby
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.shared.domain.nav.ScopeScreen
import io.middlepoint.morestuff.shared.domain.nav.ScopeScreen.Create
import io.middlepoint.morestuff.shared.domain.nav.ScopeScreen.Edit
import io.middlepoint.morestuff.shared.domain.nav.ScopeScreen.Root
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesUiEvent.CreateScope
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesUiEvent.DeleteScope
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesUiEvent.ReorderScopes
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesUiEvent.UpdateScopeName
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import io.middlepoint.morestuff.shared.ui.theme.md_theme_light_error
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.channels.Channel
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.cd_add_new_scope
import morestuff.composeapp.generated.resources.cd_dots_menu
import morestuff.composeapp.generated.resources.cd_move_icon
import morestuff.composeapp.generated.resources.cd_navigate_back
import morestuff.composeapp.generated.resources.cd_scopes_icon
import morestuff.composeapp.generated.resources.delete
import morestuff.composeapp.generated.resources.delete_scope
import morestuff.composeapp.generated.resources.description_scopes
import morestuff.composeapp.generated.resources.edit_scope
import morestuff.composeapp.generated.resources.sure_delete_scope
import morestuff.composeapp.generated.resources.title_scopes
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ScopesScreen(
  onBack: () -> Unit,
  initialScreen: ScopeScreen = Root,
) {

  val router = rememberRouter { listOf(initialScreen) }
  val viewModel = koinInjectOnRoute(ScopesViewModel::class)
  val model by viewModel.models.collectAsState()

  RoutedContent(
    router = router,
    animation = stackAnimation(slide())
  ) { screen ->
    when (screen) {
      Root -> ScopesContent(
        model = model,
        onBack = onBack,
        onCreateScope = { router.push(Create) },
        onEditScope = { scope -> router.push(Edit(scope)) },
        onEvent = viewModel::take
      )

      Create -> CreateScopeScreen(
        onBack = router::pop,
        onSaveScope = { title ->
          viewModel.take(CreateScope(title))
          router.pop()
        }
      )

      is Edit -> EditScopeScreen(
        scope = screen.scope,
        onBack = router::pop,
        onSaveScope = { title ->
          viewModel.take(UpdateScopeName(screen.scope.id, title))
          router.pop()
        }
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopesContent(
  model: ScopesState,
  onBack: () -> Unit,
  onCreateScope: () -> Unit,
  onEditScope: (ScopeDomain) -> Unit,
  onEvent: (ScopesUiEvent) -> Unit,
) {

  var isDeleteDialogOpen by remember { mutableStateOf(false) }
  var selectedScope by remember { mutableStateOf<ScopeDomain?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = (stringResource(Res.string.title_scopes))) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.cd_navigate_back)
            )
          }
        },
        actions = {
          IconButton(onClick = onCreateScope) {
            Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = stringResource(Res.string.cd_add_new_scope)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(it),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {

      Icon(
        imageVector = Icons.Filled.ModeStandby,
        contentDescription = stringResource(Res.string.cd_scopes_icon),
        modifier = Modifier
          .padding(top = 20.dp)
          .size(60.dp),
        tint = MaterialTheme.colorScheme.onSurface
      )

      Spacer(
        modifier = Modifier.height(16.dp)
      )

      Text(
        text = stringResource(Res.string.description_scopes),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(
        modifier = Modifier.height(16.dp)
      )

      OrderedScopesList(
        model = model,
        onEditScope = onEditScope,
        onDeleteScope = { scopeId ->
          selectedScope = scopeId
          isDeleteDialogOpen = true
        },
        onEvent = onEvent
      )
    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrderedScopesList(
  model: ScopesState,
  onEditScope: (ScopeDomain) -> Unit,
  onDeleteScope: (ScopeDomain) -> Unit,
  onEvent: (ScopesUiEvent) -> Unit
) {

  var scopes by remember(model.scopes) { mutableStateOf(model.scopes) }

  val menuVisibility = remember(scopes) {
    mutableStateMapOf<Long, Boolean>().apply {
      scopes.forEach { scope ->
        put(scope.id, false)
      }
    }
  }

  val listState = rememberLazyListState()

  fun updateList(from: Int, to: Int) {
    val newList = scopes.toMutableList().apply {
      val movedScope = removeAt(from)
      add(to, movedScope)
    }
    scopes = newList
  }

  val listUpdatedChannel = remember { Channel<Unit>(Channel.CONFLATED) }

  val reorderState = rememberReorderableLazyListState(listState) { from, to ->
    listUpdatedChannel.tryReceive()
    updateList(from.index, to.index)
    listUpdatedChannel.receive()
  }

  LaunchedEffect(scopes) {
    listUpdatedChannel.trySend(Unit)
  }

  LaunchedEffect(scopes) {
    if (scopes != model.scopes) {
      onEvent(ReorderScopes(scopes))
    }
  }

  Column(
    modifier = Modifier.padding(top = 20.dp)
  ) {
    Text(
      text = stringResource(Res.string.title_scopes),
      modifier = Modifier.padding(start = 16.dp),
      color = MaterialTheme.colorScheme.primary,
      style = MaterialTheme.typography.bodyLarge
    )

    Spacer(
      modifier = Modifier.height(10.dp)
    )

    LazyColumn(
      state = listState,
      modifier = Modifier.fillMaxSize()
    ) {
      itemsIndexed(
        items = scopes,
        key = { _, scope -> scope.id }
      ) { _, scope ->
        ReorderableItem(reorderState, key = scope.id) {
          Column(
            modifier = Modifier.background(
              color = MaterialTheme.colorScheme.surfaceContainerElevation
            )
          ) {
            ListItem(
              modifier = Modifier
                .fillMaxWidth()
                .longPressDraggableHandle(),
              leadingContent = {
                Icon(
                  imageVector = Icons.Default.DragHandle,
                  contentDescription = stringResource(Res.string.cd_move_icon),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.draggableHandle()
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
                      contentDescription = stringResource(Res.string.cd_dots_menu),
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
                    text = { Text(text = stringResource(Res.string.edit_scope)) },
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
                    text = { Text(text = stringResource(Res.string.delete)) },
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

            HorizontalDivider(
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
private fun DeleteScopeDialog(
  onDismissRequest: () -> Unit,
  scopeName: String,
  onConfirm: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = { Text(stringResource(Res.string.delete_scope)) },
    text = {
      Text(
        text = stringResource(Res.string.sure_delete_scope).replace("%s", scopeName),
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
        Text(stringResource(Res.string.delete))
      }
    },
    dismissButton = {
      Button(
        onClick = onDismissRequest
      ) {
        Text(stringResource(Res.string.cancel))
      }
    }
  )
}
