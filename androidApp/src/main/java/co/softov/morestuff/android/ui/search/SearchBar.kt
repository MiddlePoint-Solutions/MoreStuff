package co.softov.morestuff.android.ui.search

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.FilterType
import co.softov.morestuff.android.ui.schedule.PriorityItem
import org.koin.androidx.compose.koinViewModel

@ExperimentalMaterial3Api
@Composable
fun SearchBar(
    onSearchClose: () -> Unit,
    showTaskChat: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {

    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val searchResult by viewModel.searchResults.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        isSearchActive = true
        focusRequester.requestFocus()
    }

    val exitSearch by rememberUpdatedState(
        newValue = {
            onSearchClose()
            viewModel.reset()
        }
    )

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        query = viewModel.query,
        onQueryChange = viewModel::setSearchQuery,
        onSearch = { },
        active = isSearchActive,
        onActiveChange = { isActive ->
            if (!isActive) {
                exitSearch()
            }
        },
        placeholder = { Text(text = stringResource(R.string.search)) },
        leadingIcon = {
            IconButton(onClick = {
                exitSearch()
            }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.cd_navigate_back)
                )
            }
        },
        trailingIcon = {
            if (viewModel.query.isNotEmpty()) {
                IconButton(onClick = viewModel::clearSearchQuery) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_clear_search_query)
                    )
                }
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {


        Column {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                SearchFilterChip(
                    filter = FilterType.Scheduled,
                    selectedFilter = viewModel.filter,
                    onFilterSelected = viewModel::setSearchFilter
                )

                SearchFilterChip(
                    filter = FilterType.Reminder,
                    selectedFilter = viewModel.filter,
                    onFilterSelected = viewModel::setSearchFilter
                )

                SearchFilterChip(
                    filter = FilterType.Done,
                    selectedFilter = viewModel.filter,
                    onFilterSelected = viewModel::setSearchFilter
                )
            }

            Crossfade(
                targetState = searchResult.isEmpty() && viewModel.query.isNotEmpty(),
                label = "Search results fade animation"
            ) {
                when (it) {
                    true -> {
                        Text(
                            text = stringResource(R.string.no_results_found),
                            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                        )
                    }

                    false -> {
                        Crossfade(
                            targetState = searchResult,
                            label = "Search results fade"
                        ) { result ->
                            LazyColumn {
                                items(
                                    items = result,
                                    key = { item -> item.id },
                                    contentType = { item ->
                                        when (item.isComplete) {
                                            true -> SearchContentType.Complete
                                            false -> SearchContentType.Priority
                                        }
                                    }
                                ) { task ->
                                    when {
                                        task.isComplete -> CompletePriorityItem(
                                            task = task,
                                            onClick = { taskId ->
                                                showTaskChat(taskId)
                                            }
                                        )

                                        else -> PriorityItem(
                                            task = task,
                                            onClick = { taskId ->
                                                showTaskChat(taskId)
                                            }
                                        )
                                    }

                                    Divider(
                                        thickness = 0.5.dp,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class SearchContentType {
    Priority, Complete
}

@ExperimentalMaterial3Api
@Composable
private fun SearchFilterChip(
    filter: FilterType,
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit,
) {

    val isSelected by remember(selectedFilter) {
        derivedStateOf { filter == selectedFilter }
    }

    val selectedColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color(0xFF8C98FF)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = ""
    )

    val unselectedColor by animateColorAsState(
        targetValue = if (!isSelected) {
            Color.Transparent
        } else {
            Color(0xFF8C98FF)
        },
        animationSpec = tween(durationMillis = 200),
        label = ""
    )

    FilterChip(
        selected = filter == selectedFilter,
        onClick = {
            onFilterSelected(filter)
        },
        label = { filter.Title() },
        leadingIcon = { filter.Icon() },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = unselectedColor,
            selectedContainerColor = selectedColor,
        ),
        modifier = Modifier.padding(end = 10.dp),
        shape = CircleShape
    )
}

@Composable
private fun FilterType.Title() {
    val title = when (this) {
        FilterType.None -> ""
        FilterType.Scheduled -> stringResource(R.string.filter_scheduled)
        FilterType.Reminder -> stringResource(R.string.filter_reminder)
        FilterType.Done -> stringResource(R.string.filter_done)
    }
    Text(text = title)
}

@Composable
private fun FilterType.Icon() {
    when (this) {
        FilterType.None -> Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = "",
        )

        FilterType.Scheduled -> Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = stringResource(R.string.cd_filter_schedules_tasks),
        )

        FilterType.Reminder -> Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = stringResource(R.string.cd_filter_reminders_tasks),
        )

        FilterType.Done -> Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = stringResource(R.string.cd_filter_completed_tasks),
        )
    }
}