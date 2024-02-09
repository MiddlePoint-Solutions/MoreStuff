package co.softov.morestuff.android.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.scopes.CreateScopeButton
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopeSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    addSelectedTasksToScope: (Long) -> Unit,
    scopes: List<ScopeDomain>,
    sheetState: SheetState,
) {
    val coroutineScope = rememberCoroutineScope()
    val navigation = LocalAppNavigation.current
    ModalBottomSheet(
        content = {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                ScopeList(
                    scopes = scopes,
                    onScopeSelected = addSelectedTasksToScope,
                    modifier = Modifier.height(216.dp),
                    hideSheet = {
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }
                )
                CreateScopeButton(
                    onClick = {
                        navigation.push(Screen.CreateScope)
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismissRequest()
                        }
                    }
                )
            }
        },
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    )
}

@Composable
private fun ScopeList(
    scopes: List<ScopeDomain>,
    onScopeSelected: (Long) -> Unit,
    modifier: Modifier,
    hideSheet: () -> Unit,
) {
    val scrollState = rememberLazyListState()

    Text(
        text = (stringResource(R.string.add_to_scope)),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp,
        fontWeight = FontWeight(400),
        modifier = Modifier.padding(start = 10.dp)
    )
    LazyColumn(
        state = scrollState
    ) {
        items(
            items = scopes,
            key = { "Scope${it.id}" }
        ) { scope ->
            ListItem(
                modifier = Modifier.clickable {
                    onScopeSelected(scope.id)
                    hideSheet()
                },
                headlineContent = {
                    Text(scope.name)
                },
            )
            if (scopes.last().id == scope.id) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = Dp.Hairline
                )
            }
        }
    }
}