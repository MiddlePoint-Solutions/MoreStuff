package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopeTitleEditor
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.description_create_scope
import morestuff.composeapp.generated.resources.save
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

  LaunchedEffect(sheetState.currentValue) {
    if (sheetState.currentValue == SheetValue.Hidden) {
      keyboardController?.hide()
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
    dragHandle = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(
          onClick = {
            if (scopeTitle.isNotBlank()) {
              onConfirm(scopeTitle.trim())
              keyboardController?.hide()
            }
          },
          enabled = showSaveAction
        ) {
          Text(text = stringResource(Res.string.save).uppercase())
        }
      }
    }
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().height(150.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {

      Text(
        text = stringResource(Res.string.description_create_scope),
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
      )

      Spacer(
        modifier = Modifier.height(16.dp)
      )
    }

    ScopeTitleEditor(
      title = scopeTitle,
      onTitleChange = { title -> scopeTitle = title },
      modifier = Modifier.focusRequester(focusRequester)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.End
    ) {
      Text(
        text = "${(scopeTitle.length)}/12",
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        style = MaterialTheme.typography.bodyLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          textAlign = TextAlign.End
        ),
        modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
      )
    }
  }
}
