package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopeTitleEditor
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.cd_scopes_icon
import morestuff.composeapp.generated.resources.description_create_scope
import morestuff.composeapp.generated.resources.ic_scope_add
import morestuff.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScopeBottomSheet(
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  onConfirm: (String) -> Unit,
) {
  var scopeTitle by remember { mutableStateOf("") }
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current
  var showSaveAction by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    snapshotFlow { scopeTitle }
      .distinctUntilChanged()
      .collectLatest { title -> showSaveAction = title.isNotBlank() }
  }

  LaunchedEffect(sheetState) {
    snapshotFlow { sheetState.currentValue }
      .filter { it == SheetValue.Expanded }
      .first()
    delay(300)
    focusRequester.requestFocus()
    keyboardController?.show()
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        painter = painterResource(Res.drawable.ic_scope_add),
        contentDescription = stringResource(Res.string.cd_scopes_icon),
        modifier = Modifier
          .size(50.dp),
        tint = MaterialTheme.colorScheme.onSurface
      )

      Spacer(
        modifier = Modifier.height(16.dp)
      )

      Text(
        text = stringResource(Res.string.description_create_scope),
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
      )

      Spacer(
        modifier = Modifier.height(16.dp)
      )
    }

    Box {
      ScopeTitleEditor(
        title = scopeTitle,
        onTitleChange = { title -> scopeTitle = title.trim() },
        modifier = Modifier.focusRequester(focusRequester)
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
      Button(
        onClick = {
          if (scopeTitle.isNotBlank()) {
            onConfirm(scopeTitle.trim())
            keyboardController?.hide()
          }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = showSaveAction
      ) {
        Text(text = stringResource(Res.string.save))
      }

      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = {
          keyboardController?.hide()
          onDismissRequest()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
      ) {
        Text(text = stringResource(Res.string.cancel))
      }

      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}