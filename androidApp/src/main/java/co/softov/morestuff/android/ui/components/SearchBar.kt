package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.schedule.PriorityItem
import co.softov.morestuff.android.ui.schedule.PriorityViewModel
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

    val viewModel: PriorityViewModel = getViewModel()

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = searchText.value,
        onQueryChange = { newText ->
            searchText.value = newText
            viewModel.searchTasks(newText)
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
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Button(onClick = { viewModel.loadActiveTasksWithSchedule() }) {
                    Text("Scheduled")
                }

                Button(onClick = { viewModel.loadTasksWithReminder() }) {
                    Text("Reminder")
                }

                Button(onClick = { viewModel.loadCompletedTasks() }) {
                    Text("Done")
                }
            }
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