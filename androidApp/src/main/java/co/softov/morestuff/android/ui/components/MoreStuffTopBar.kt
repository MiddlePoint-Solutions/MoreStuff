package co.softov.morestuff.android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.softov.morestuff.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffTopBar(
    showSettings: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            IconButton(onClick = showSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    )
}