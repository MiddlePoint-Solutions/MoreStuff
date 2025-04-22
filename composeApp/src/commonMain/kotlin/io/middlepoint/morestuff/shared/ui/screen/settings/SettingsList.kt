package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink
import io.middlepoint.morestuff.shared.ui.components.AppSettingValueState
import io.middlepoint.morestuff.shared.ui.components.rememberAppSettingState
import io.middlepoint.morestuff.shared.ui.theme.surfaceContainerElevation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsList(
  modifier: Modifier = Modifier,
  state: AppSettingValueState<Int>,
  title: @Composable () -> Unit,
  items: List<String>,
  icon: (@Composable () -> Unit)? = null,
  subtitle: (@Composable () -> Unit)? = null,
  closeDialogDelay: Long = 200,
  action: (@Composable () -> Unit)? = null,
) {

  var showSheet by remember { mutableStateOf(false) }

  SettingsMenuLink(
    modifier = modifier,
    icon = icon,
    title = title,
    subtitle = subtitle,
    action = action,
    onClick = { showSheet = true },
    colors = ListItemDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer
    )
  )

  if (showSheet) {

    if (state.value >= items.size) {
      throw IndexOutOfBoundsException("El valor actual para $title no puede ser mayor que el tamaño de la lista")
    }

    val coroutineScope = rememberCoroutineScope()
    val onSelected: (Int) -> Unit = { selectedIndex ->
      coroutineScope.launch {
        state.value = selectedIndex
        delay(closeDialogDelay)
        showSheet = false
      }
    }

    ModalBottomSheet(
      onDismissRequest = { showSheet = false },
      containerColor = MaterialTheme.colorScheme.surfaceContainerElevation
    ) {
      Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerElevation
      ) {
        Column(
          modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
          verticalArrangement = Arrangement.Center
        ) {

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            title()
          }

          items.forEachIndexed { index, item ->
            val isSelected by rememberUpdatedState(newValue = state.value == index)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .selectable(
                  selected = isSelected,
                  onClick = { if (!isSelected) onSelected(index) }
                )
                .padding(
                  start = 32.dp,
                  end = 32.dp,
                ),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { if (!isSelected) onSelected(index) }
              )
              Text(
                text = item,
                style = MaterialTheme.typography.bodyLarge.copy(
                  color = MaterialTheme.colorScheme.onSurface
                ),
              )
            }
          }
        }
      }
    }
  }
}

@Preview
@Composable
internal fun ListLinkPreview() {
  MaterialTheme {
    SettingsList(
      items = listOf("Banana", "Kiwi", "Pineapple"),
      state = rememberAppSettingState({ 1 }, { }),
      icon = { Icon(imageVector = Icons.Default.Wifi, contentDescription = "Wifi") },
      title = { Text(text = "Hello") },
      subtitle = { Text(text = "This is a longer text") },
    )
  }
}