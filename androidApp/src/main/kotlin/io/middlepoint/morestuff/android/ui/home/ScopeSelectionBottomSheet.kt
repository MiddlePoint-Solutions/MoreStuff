package io.middlepoint.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.android.R
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import io.middlepoint.morestuff.shared.domain.model.defaultScope
import io.middlepoint.morestuff.android.ui.theme.MoreStuffTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopeSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    addSelectedTasksToScope: (Long) -> Unit,
    createNewScope: () -> Unit,
    scopes: List<ScopeDomain>,
    sheetState: SheetState,
) {

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
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
    scopes: List<ScopeDomain>,
    onScopeSelected: (Long) -> Unit,
    createNewScope: () -> Unit,
) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.choose_scope),
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            actions = {
                IconButton(onClick = createNewScope) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.cd_add_new_scope),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        LazyColumn {
            items(
                items = scopes,
                key = { scope -> "Scope${scope.id}" }
            ) { scope ->
                ListItem(
                    modifier = Modifier.clickable { onScopeSelected(scope.id) },
                    headlineContent = { Text(scope.name) },
                )
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
private fun Preview() {
    MoreStuffTheme {
        ScopeSelection(
            scopes = listOf(defaultScope),
            onScopeSelected = {},
            createNewScope = {}
        )
    }

}
