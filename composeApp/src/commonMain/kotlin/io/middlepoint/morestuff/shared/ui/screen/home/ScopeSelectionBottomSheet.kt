package io.middlepoint.morestuff.shared.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.domain.model.core.Scope
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_add_new_scope
import morestuff.composeapp.generated.resources.choose_scope
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopeSelectionBottomSheet(
  onDismissRequest: () -> Unit,
  addSelectedTasksToScope: (Long) -> Unit,
  createNewScope: () -> Unit,
  scopes: List<Scope>,
  sheetState: SheetState,
) {

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
    content = {
      ScopeSelection(
        scopes = scopes,
        onScopeSelected = {
          addSelectedTasksToScope(it)
          onDismissRequest()
        },
        createNewScope = {
          createNewScope()
          onDismissRequest()
        },
      )
    },
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScopeSelection(
  scopes: List<Scope>,
  onScopeSelected: (Long) -> Unit,
  createNewScope: () -> Unit,
) {
  Column {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        ,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
      ) {
        Text(
          text = stringResource(Res.string.choose_scope),
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.headlineSmall,
          modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
          onClick = createNewScope,
          modifier = Modifier.align(Alignment.CenterEnd)
        ) {
          Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(Res.string.cd_add_new_scope),
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

    }


    LazyColumn {
      items(
        items = scopes,
        key = { scope -> "Scope${scope.id}" }
      ) { scope ->
        ListItem(
          modifier = Modifier.clickable { onScopeSelected(scope.id) },
          headlineContent = { Text(scope.name) },
          colors = androidx.compose.material3.ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
          )
        )
      }
    }
  }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        ScopeSelection(
//            scopes = listOf(defaultScope),
//            onScopeSelected = {},
//            createNewScope = {}
//        )
//    }
//}
