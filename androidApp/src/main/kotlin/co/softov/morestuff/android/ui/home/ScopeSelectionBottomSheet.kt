package co.softov.morestuff.android.ui.home

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.domain.model.defaultScope
import co.softov.morestuff.android.domain.nav.Screen
import co.softov.morestuff.android.ui.local.LocalAppNavigation
import co.softov.morestuff.android.ui.components.CreateScopeButton
import co.softov.morestuff.android.ui.home.ScopeSelectionState.*
import co.softov.morestuff.android.ui.scopes.ScopeTitleEditor
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.arkivanov.decompose.router.stack.push
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScopeSelectionBottomSheet(
    onDismissRequest: () -> Unit,
    addSelectedTasksToScope: (Long) -> Unit,
    createNewScope: (String) -> Unit,
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
                    createNewScope(it)
                    onDismissRequest()
                },
            )
        },
    )
}

enum class ScopeSelectionState {
    Select, Create
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ScopeSelection(
    scopes: List<ScopeDomain>,
    onScopeSelected: (Long) -> Unit,
    createNewScope: (String) -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    val state = rememberPagerState { ScopeSelectionState.entries.size }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Box {
        HorizontalPager(
            state = state,
            modifier = Modifier
                .animateContentSize(spring()),
            key = { entries[it].name },
            userScrollEnabled = false
        ) {

            when (entries[it]) {
                Select -> {
                    Column {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.title_create_scope),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
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

                        CreateScopeButton(
                            onClick = {
                                coroutineScope.launch {
                                    state.animateScrollToPage(Create.ordinal)
                                    focusManager.moveFocus(FocusDirection.Next)
                                }
                            }
                        )
                    }
                }

                Create -> {

                    var scopeTitle by remember { mutableStateOf("") }
                    var showSaveAction by remember { mutableStateOf(false) }

                    val onBack: () -> Unit by rememberUpdatedState {
                        focusManager.clearFocus()
                        coroutineScope.launch {
                            delay(300)
                            state.animateScrollToPage(Select.ordinal)
                        }
                    }

                    BackHandler(onBack = onBack)

                    LaunchedEffect(Unit) {
                        delay(350)
                        focusRequester.requestFocus()

                        snapshotFlow { scopeTitle }
                            .distinctUntilChanged()
                            .collectLatest { title -> showSaveAction = title.isNotBlank() }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.title_create_scope),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = onBack
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.cd_navigate_back)
                                    )
                                }
                            },
                            actions = {
                                if (showSaveAction) {
                                    TextButton(
                                        onClick = { createNewScope(scopeTitle) },
                                        contentPadding = PaddingValues()
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.save).uppercase(),
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        ScopeTitleEditor(
                            title = scopeTitle,
                            onTitleChange = { title -> scopeTitle = title },
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .focusRequester(focusRequester),
                        )
                    }

                }
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
