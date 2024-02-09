package co.softov.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.components.CreateScopeButton
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.arkivanov.decompose.router.stack.push

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopeSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    addSelectedTasksToScope: (Long) -> Unit,
    scopes: List<ScopeDomain>,
    sheetState: SheetState,
) {
    val navigation = LocalAppNavigation.current

    ModalBottomSheet(
        content = {
            ScopeList(
                scopes = scopes,
                onScopeSelected = {
                    addSelectedTasksToScope(it)
                    onDismissRequest()
                },
                createNewScope = {
                    navigation.push(Screen.CreateScope)
                    onDismissRequest()
                },
            )

        },
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    )
}

@Composable
private fun ScopeList(
    scopes: List<ScopeDomain>,
    onScopeSelected: (Long) -> Unit,
    createNewScope: () -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.add_to_scope),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        LazyColumn {
            items(
                items = scopes,
                key = { "Scope${it.id}" }
            ) { scope ->
                ListItem(
                    modifier = Modifier.clickable { onScopeSelected(scope.id) },
                    headlineContent = { Text(scope.name) },
                )
            }
        }

        CreateScopeButton(onClick = createNewScope)
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
        ScopeList(
            scopes = listOf(defaultScope),
            onScopeSelected = {},
            createNewScope = {}
        )
    }

}
