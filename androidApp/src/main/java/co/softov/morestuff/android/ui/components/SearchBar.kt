package co.softov.morestuff.android.ui.components

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
import androidx.compose.runtime.MutableState
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
    searchText: MutableState<String>,
    isSearching: MutableState<Boolean>,
    onSearchClose: () -> Unit,
    modifier: Modifier = Modifier,
    showTaskChat: (taskId: Long) -> Unit,
) {

    val viewModel: SearchViewModel = getViewModel()
    var filterSelected by remember { mutableStateOf(Filter.None) }
    val searchBarFocusState = rememberSaveable { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            if (!searchBarFocusState.value) {
                viewModel.clearResults()
            }
        }
    }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .focusable(true)
            .onFocusChanged { searchBarFocusState.value = it.isFocused },
        query = searchText.value,
        onQueryChange = { newText ->
            searchText.value = newText
            viewModel.searchTasks(newText, filterSelected)
        },
        onSearch = { _ -> isSearching.value = false },
        active = isSearching.value,
        onActiveChange = { isActive ->
            isSearching.value = isActive
        },
        placeholder = { Text(text = "Search...") },
        trailingIcon = {
            IconButton(onClick = {
                onSearchClose()
                searchText.value = ""
                viewModel.searchResults.value = listOf()
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
                    selected = filterSelected == Filter.Scheduled,
                    onClick = {
                        if (filterSelected == Filter.Scheduled) {
                            filterSelected = Filter.None
                            viewModel.clearResults()
                        } else {
                            filterSelected = Filter.Scheduled
                            viewModel.clearResults()
                            viewModel.loadActiveTasksWithOneTimeSchedule()
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
                    selected = filterSelected == Filter.Reminder,
                    onClick = {
                        if (filterSelected == Filter.Reminder) {
                            filterSelected = Filter.None
                            viewModel.clearResults()
                        } else {
                            filterSelected = Filter.Reminder
                            viewModel.clearResults()
                            viewModel.loadTasksWithReminderSchedule()
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
                    selected = filterSelected == Filter.Done,
                    onClick = {
                        if (filterSelected == Filter.Done) {
                            filterSelected = Filter.None
                            viewModel.clearResults()
                        } else {
                            filterSelected = Filter.Done
                            viewModel.clearResults()
                            viewModel.loadCompletedTasks()
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
            if (viewModel.searchResults.value.isEmpty() && searchText.value.isNotEmpty()) {
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