package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.cd_navigate_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigateBackIconButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.cd_navigate_back),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}