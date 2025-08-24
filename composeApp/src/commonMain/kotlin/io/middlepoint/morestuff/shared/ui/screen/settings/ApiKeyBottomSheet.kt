package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.api_key_label
import morestuff.composeapp.generated.resources.api_key_title
import morestuff.composeapp.generated.resources.cancel
import morestuff.composeapp.generated.resources.save
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyBottomSheet(
  isVisible: Boolean,
  onDismiss: () -> Unit,
  currentApiKey: String,
  onSave: (String) -> Unit
) {
  if (!isVisible) return

  val sheetState = rememberModalBottomSheetState()
  var apiKeyText by remember(currentApiKey) { mutableStateOf(currentApiKey) }
  var isPasswordVisible by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerElevation,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(Res.string.api_key_title),
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.onSurface
      )

      OutlinedTextField(
        value = apiKeyText,
        onValueChange = { apiKeyText = it },
        label = { Text(stringResource(Res.string.api_key_label)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPasswordVisible)
          VisualTransformation.None
        else
          PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            onSave(apiKeyText)
            onDismiss()
          }
        ),
        trailingIcon = {
          IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
              imageVector = if (isPasswordVisible)
                Icons.Default.VisibilityOff
              else
                Icons.Default.Visibility,
              contentDescription = if (isPasswordVisible)
                "Hide API Key"
              else
                "Show API Key"
            )
          }
        }
      )

      Spacer(modifier = Modifier.height(24.dp))
      Button(
        onClick = {
          onSave(apiKeyText)
          onDismiss()
        },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      ) {
        Text(stringResource(Res.string.save))
      }
      Spacer(modifier = Modifier.height(16.dp))
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
      ) {
        Text(stringResource(Res.string.cancel))
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}