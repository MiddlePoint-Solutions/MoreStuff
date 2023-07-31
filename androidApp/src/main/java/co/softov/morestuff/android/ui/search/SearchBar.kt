package co.softov.morestuff.android.ui.search

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.enums.Filter
import co.softov.morestuff.android.ui.schedule.PriorityItem
import org.koin.androidx.compose.getViewModel


@ExperimentalMaterial3Api
@Composable
fun CustomSearchBar(
    onSearchClose: () -> Unit,
    modifier: Modifier = Modifier,
    showTaskChat: (taskId: Long) -> Unit,
) {
    val viewModel: SearchViewModel = getViewModel()
    val filterSelected by remember { mutableStateOf(Filter.None) }
    val searchBarFocusState = rememberSaveable { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            if (!searchBarFocusState.value) {
                viewModel.currentFilter
            }
        }
    }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .focusable(true)
            .onFocusChanged { searchBarFocusState.value = it.isFocused },
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
        placeholder = { Text(text = "Search...") },
        trailingIcon = {
            IconButton(onClick = {
                onSearchClose()
                searchText = ""
                viewModel.searchResults.value = listOf()
                viewModel.setFilter(Filter.None)
            }) {
                Icon(Icons.Default.Close, contentDescription = "Close icon")
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon"
            )
        },

        ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 10.dp, end = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = viewModel.currentFilter == Filter.Scheduled,
                    onClick = {
                        if (viewModel.currentFilter == Filter.Scheduled) {
                            viewModel.setFilter(Filter.None)
                        } else {
                            viewModel.setFilter(Filter.Scheduled)
                        }
                    },
                    label = { Text("Scheduled") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (filterSelected == Filter.Scheduled) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8C98FF),
                    ),
                    modifier = Modifier.padding(end = 10.dp),
                    shape = shape
                )

                FilterChip(
                    selected = viewModel.currentFilter == Filter.Reminder,
                    onClick = {
                        if (viewModel.currentFilter == Filter.Reminder) {
                            viewModel.setFilter(Filter.None)
                        } else {
                            viewModel.setFilter(Filter.Reminder)
                        }
                    },
                    label = { Text("Reminder") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (filterSelected == Filter.Reminder) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8C98FF),
                    ),
                    modifier = Modifier
                        .padding(end = 10.dp),
                    shape = shape
                )

                FilterChip(
                    selected = viewModel.currentFilter == Filter.Done,
                    onClick = {
                        if (viewModel.currentFilter == Filter.Done) {
                            viewModel.setFilter(Filter.None)
                        } else {
                            viewModel.setFilter(Filter.Done)
                        }
                    },
                    label = { Text("Done") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = if (filterSelected == Filter.Done) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8C98FF),
                    ),
                    shape = shape
                )
            }
            if (viewModel.searchResults.value.isEmpty() && searchText.isNotEmpty()) {
                Text(
                    text = "Task not found",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            } else {
                for (task in viewModel.searchResults.value) {
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