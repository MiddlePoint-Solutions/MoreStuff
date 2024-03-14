package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R

@Composable
fun CreateScopeButton(
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(50)
    ) {
        Icon(
            imageVector = Icons.Filled.AddCircle,
            contentDescription = "Create",
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = stringResource(R.string.add_scope),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}