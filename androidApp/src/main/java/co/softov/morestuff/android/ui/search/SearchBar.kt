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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
) {

    val viewModel: SearchViewModel = koinViewModel()
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val selectedFilter by viewModel.filter.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResults.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = searchText,
        onQueryChange = { newText ->
            searchText = newText
            viewModel.searchTasks(newText)
        },
        onSearch = { _ -> isSearching = false },
        active = isSearching,
        onActiveChange = { isActive ->
            isSearching = isActive
        },
        placeholder = { Text(text = stringResource(R.string.search)) },
        trailingIcon = {
            IconButton(onClick = {
                searchText = ""
                onSearchClose()
            }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.cd_navigate_back)
                )
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon"
            )
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
                    selectedFilter = selectedFilter,
                    onFilterSelected = viewModel::setFilter
                )

                SearchFilterChip(
                    filter = FilterType.Reminder,
                    selectedFilter = selectedFilter,
                    onFilterSelected = viewModel::setFilter
                )

                SearchFilterChip(
                    filter = FilterType.Done,
                    selectedFilter = selectedFilter,
                    onFilterSelected = viewModel::setFilter
                )
            }

            Crossfade(searchResult.isEmpty() && searchText.isNotEmpty(), label = "") {
                when (it) {
                    true -> {
                        Text(
                            text = "Task not found",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )
                    }

                    false -> {
                        Crossfade(
                            targetState = searchResult,
                            label = "Search results fade"
                        ) {
                            LazyColumn {
                                items(it) { task ->
                                    Divider(
                                        thickness = 0.5.dp,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    if (task.isComplete) {
                                        CompletePriorityItem(
                                            task = task,
                                            onClick = { taskId ->
                                                showTaskChat(taskId)
                                            }
                                        )
                                    } else {
                                        PriorityItem(
                                            task = task,
                                            onClick = { taskId ->
                                                showTaskChat(taskId)
                                            }
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