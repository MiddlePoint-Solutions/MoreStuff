package co.softov.morestuff.android.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialogDefaults.shape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.domain.enums.FilterName
import co.softov.morestuff.android.ui.schedule.PriorityItem
import org.koin.androidx.compose.koinViewModel


@ExperimentalMaterial3Api
@Composable
fun CustomSearchBar(
    onSearchClose: () -> Unit,
    showTaskChat: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    val viewModel: SearchViewModel = koinViewModel()
    val filterNameSelected by remember { mutableStateOf(FilterName.None) }
    var searchText by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val currentFilterName by viewModel.currentFilterName.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResults.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.reset()
        }
    }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth(),
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
                viewModel.currentFilterName.value = FilterName.None
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
                FilterChip(
                    selected = currentFilterName == FilterName.Scheduled,
                    onClick = {
                        if (currentFilterName == FilterName.Scheduled) {
                            viewModel.currentFilterName.value = FilterName.None
                        } else {
                            viewModel.currentFilterName.value = FilterName.Scheduled
                        }
                    },
                    label = { Text("Scheduled") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (filterNameSelected == FilterName.Scheduled) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8C98FF),
                    ),
                    modifier = Modifier.padding(end = 10.dp),
                    shape = shape
                )

                FilterChip(
                    selected = currentFilterName == FilterName.Reminder,
                    onClick = {
                        if (currentFilterName == FilterName.Reminder) {
                            viewModel.currentFilterName.value = FilterName.None
                        } else {
                            viewModel.currentFilterName.value = FilterName.Reminder
                        }
                    },
                    label = { Text("Reminder") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (filterNameSelected == FilterName.Reminder) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
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
                    selected = currentFilterName == FilterName.Done,
                    onClick = {
                        if (currentFilterName == FilterName.Done) {
                            viewModel.currentFilterName.value = FilterName.None
                        } else {
                            viewModel.currentFilterName.value = FilterName.Done
                        }
                    },
                    label = { Text("Done") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = if (filterNameSelected == FilterName.Done) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF8C98FF),
                    ),
                    shape = shape
                )
            }

            if (searchResult.isEmpty() && searchText.isNotEmpty()) {
                Text(
                    text = "Task not found",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            } else {

                LazyColumn {
                    items(searchResult) { task ->
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