package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.add_scope
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.save
import morestuff.composeapp.generated.resources.scope_name
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScopeBottomSheet(
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  onConfirm: (String) -> Unit,
) {
  var scopeName by remember { mutableStateOf("") }
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current

  LaunchedEffect(sheetState) {
    snapshotFlow { sheetState.currentValue }
      .filter { it == SheetValue.Expanded }
      .first()
    focusRequester.requestFocus()
    keyboardController?.show()
  }

  ModalBottomSheet(
    onDismissRequest = onDismissRequest,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(Res.string.add_scope),
        style = MaterialTheme.typography.titleLarge.copy(
          color = MaterialTheme.colorScheme.onSurface,
          fontSize = 24.sp
        ),
        modifier = Modifier.padding(bottom = 16.dp)
      )

      OutlinedTextField(
        value = scopeName,
        onValueChange = { scopeName = it },
        label = { Text(stringResource(Res.string.scope_name)) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 24.dp)
          .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
          onDone = {
            if (scopeName.isNotBlank()) {
              onConfirm(scopeName)
              keyboardController?.hide()
            }
          }
        ),
        shape = RoundedCornerShape(16.dp)
      )

      Button(
        onClick = {
          if (scopeName.isNotBlank()) {
            onConfirm(scopeName)
            keyboardController?.hide()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        enabled = scopeName.isNotBlank()
      ) {
        Text(text = stringResource(Res.string.save))
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          keyboardController?.hide()
          onDismissRequest()
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
      ) {
        Text(text = stringResource(Res.string.cancel))
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }

}