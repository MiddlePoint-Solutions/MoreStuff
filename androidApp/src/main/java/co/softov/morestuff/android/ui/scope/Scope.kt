package co.softov.morestuff.android.ui.scope

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.home.HomeContent
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.theme.surfaceContainer
import com.arkivanov.decompose.router.stack.push
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber



@Composable
fun ScopesScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: ScopeViewModel = koinViewModel()

    val uiState = viewModel.uiState.collectAsState()

    val navigation = LocalAppNavigation.current
    val snackbarHostState = remember { SnackbarHostState() }

    if (uiState.value.scopes.isNotEmpty()) {
        Column(modifier) {
            Spacer(Modifier.height(8.dp))

            uiState.value.selectedScopeId?.let { selectedScopeId ->
                val selectedScope = uiState.value.scopes.find { it.scopeId == selectedScopeId }
                selectedScope?.let {
                    ScopeTabs(
                        scopes = uiState.value.scopes,
                        selectedScope = it,
                        onScopeSelected = { scope -> viewModel.handleEvent(ScopeUiEvent.SelectScope(scope.scopeId)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Timber.d("Selected Scope ID: ${uiState.value.selectedScopeId}")
            Timber.d("Scopes: ${uiState.value.scopes}")

            Spacer(Modifier.height(8.dp))

            Crossfade(
                targetState = uiState.value.selectedScopeId,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), label = ""
            ) { scopeId ->
                scopeId?.let {
                    HomeContent(
                        showTaskChat = { taskId -> navigation.push(Screen.TaskChat(taskId)) },
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun ScopeTabs(
    scopes: List<ScopeDomain>,
    selectedScope: ScopeDomain,
    onScopeSelected: (ScopeDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = scopes.indexOfFirst { it == selectedScope }


    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = selectedTabColor
            )
        },
    ) {
        scopes.forEachIndexed { index, scope ->
            Timber.d("Creating tab for scope: ${scope.name}")
            Tab(
                selected = index == selectedIndex,
                onClick = { onScopeSelected(scope) },
                text = {
                    Text(
                        text = scope.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (index == selectedIndex) selectedTabColor else unselectedTabColor
                    )
                }
            )
        }
    }
}
