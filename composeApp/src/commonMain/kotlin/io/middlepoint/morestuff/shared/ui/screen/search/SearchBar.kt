package io.middlepoint.morestuff.shared.ui.screen.search

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.domain.enums.FilterType
import io.middlepoint.morestuff.shared.ui.components.PriorityItem
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_clear_search_query
import morestuff.composeapp.generated.resources.cd_filter_completed_tasks
import morestuff.composeapp.generated.resources.cd_filter_reminders_tasks
import morestuff.composeapp.generated.resources.cd_filter_schedules_tasks
import morestuff.composeapp.generated.resources.cd_navigate_back
import morestuff.composeapp.generated.resources.filter_done
import morestuff.composeapp.generated.resources.filter_reminder
import morestuff.composeapp.generated.resources.filter_scheduled
import morestuff.composeapp.generated.resources.ic_check_circle
import morestuff.composeapp.generated.resources.ic_reminder
import morestuff.composeapp.generated.resources.ic_schedule
import morestuff.composeapp.generated.resources.no_results_found
import morestuff.composeapp.generated.resources.search
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@ExperimentalMaterial3Api
@Composable
fun SearchBar(
  onSearchClose: () -> Unit,
  showTaskChat: (taskId: Long) -> Unit,
  modifier: Modifier = Modifier,
) {

  val viewModel = koinInjectOnRoute(SearchViewModel::class)

  val model by viewModel.models.collectAsState()
  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  val focusRequester = remember { FocusRequester() }
  var searchQuery by remember { mutableStateOf(model.query) }
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current

  LaunchedEffect(Unit) {
    isSearchActive = true
    focusRequester.requestFocus()
  }

  val exitSearch by rememberUpdatedState(
    newValue = {
      onSearchClose()
      viewModel.take(SearchEvent.ResetSearch)
    }
  )

  var searchJob by remember { mutableStateOf<Job?>(null) }
  LaunchedEffect(searchQuery) {
    searchJob?.cancel()
    searchJob = launch {
      viewModel.take(SearchEvent.SetSearchQuery(searchQuery))
    }
  }


  val onActiveChange: (Boolean) -> Unit = { isActive ->
    if (!isActive) {
      exitSearch()
    }
  }
  val colors = SearchBarDefaults.colors(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        focusManager.clearFocus()
        keyboardController?.hide()
      }
  ){
    SearchBar(
      inputField = {
        SearchBarDefaults.InputField(
          query = searchQuery,
          onQueryChange = { newQuery -> searchQuery = newQuery },
          onSearch = { },
          expanded = isSearchActive,
          onExpandedChange = onActiveChange,
          placeholder = { Text(text = stringResource(Res.string.search)) },
          leadingIcon = {
            IconButton(onClick = {
              exitSearch()
            }) {
              Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.cd_navigate_back)
              )
            }
          },
          trailingIcon = {
            if (model.query.isNotEmpty()) {
              IconButton(onClick = { viewModel.take(SearchEvent.ClearSearchQuery) }) {
                Icon(
                  Icons.Default.Close,
                  contentDescription = stringResource(Res.string.cd_clear_search_query)
                )
              }
            }
          },
          colors = TextFieldDefaults.colors(),
        )
      },
      expanded = isSearchActive,
      onExpandedChange = onActiveChange,
      modifier = Modifier.focusRequester(focusRequester),
      colors = colors,
      content = {
        Column {
          Row(
            modifier = Modifier
              .padding(top = 16.dp, start = 4.dp, end = 4.dp)
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {

            SearchFilterChip(
              filter = FilterType.Scheduled,
              selectedFilter = model.filter,
              onFilterSelected = { selectedFilter ->
                focusManager.clearFocus()
                keyboardController?.hide()
                viewModel.take(
                  SearchEvent.SetSearchFilter(
                    selectedFilter
                  )
                )
              }
            )

            SearchFilterChip(
              filter = FilterType.Reminder,
              selectedFilter = model.filter,
              onFilterSelected = { selectedFilter ->
                focusManager.clearFocus()
                keyboardController?.hide()
                viewModel.take(
                  SearchEvent.SetSearchFilter(
                    selectedFilter
                  )
                )
              }
            )

            SearchFilterChip(
              filter = FilterType.Done,
              selectedFilter = model.filter,
              onFilterSelected = { selectedFilter ->
                focusManager.clearFocus()
                keyboardController?.hide()
                viewModel.take(
                  SearchEvent.SetSearchFilter(
                    selectedFilter
                  )
                )
              }
            )
          }

          Crossfade(
            targetState = model.searchResults.isEmpty() && model.query.isNotEmpty(),
            label = "Search results fade animation"
          ) {
            when (it) {
              true -> {
                Text(
                  text = stringResource(Res.string.no_results_found),
                  style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                )
              }

              false -> {
                Crossfade(
                  targetState = model.searchResults,
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
                          onClick = { showTaskChat(task.id) }
                        )

                        else -> PriorityItem(
                          task = task,
                          onClick = { showTaskChat(task.id) },
                          onLongClick = {},
                          onTaskComplete = {},
                          enabled = false
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      },
    )
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
      MaterialTheme.colorScheme.primary.copy(alpha = 0.12F)
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
    modifier = Modifier.padding(end = 3.dp),
    shape = CircleShape,
  )
}

@Composable
private fun FilterType.Title() {
  val title = when (this) {
    FilterType.None -> ""
    FilterType.Scheduled -> stringResource(Res.string.filter_scheduled)
    FilterType.Reminder -> stringResource(Res.string.filter_reminder)
    FilterType.Done -> stringResource(Res.string.filter_done)
  }
  Text(
    text = title, style = TextStyle(
      fontSize = 12.sp,
    )
  )
}

@Composable
private fun FilterType.Icon() {
  when (this) {
    FilterType.None -> Icon(
      imageVector = Icons.Default.Schedule,
      contentDescription = "",
    )

    FilterType.Scheduled -> Icon(
      imageVector = vectorResource(Res.drawable.ic_schedule),
      contentDescription = stringResource(Res.string.cd_filter_schedules_tasks),
    )

    FilterType.Reminder -> Icon(
      imageVector = vectorResource(Res.drawable.ic_reminder),
      contentDescription = stringResource(Res.string.cd_filter_reminders_tasks),
    )

    FilterType.Done -> Icon(
      imageVector = vectorResource(Res.drawable.ic_check_circle),
      contentDescription = stringResource(Res.string.cd_filter_completed_tasks),
    )
  }
}