package co.softov.morestuff.android.presentation.schedule_list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import co.softov.morestuff.android.app.util.LifecycleViewModelStoreOwner
import co.softov.morestuff.android.presentation.schedule_list.model.Page
import co.softov.morestuff.android.presentation.schedule_list.schedule.TaskListItem
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ScheduleList(page: Page) {
    // TODO: Remove LifecycleViewModelStoreOwner once Koin has a fix for multiple instances of the same ViewModel
    val lifecycleOwner = LocalView.current.findViewTreeLifecycleOwner()
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides ViewModelStoreOwner {
            LifecycleViewModelStoreOwner(lifecycleOwner = lifecycleOwner).viewModelStore
        }
    ) {
        val viewModel: SchedulePageViewModel = getViewModel { parametersOf(page) }
        val state by viewModel.state.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            when {
                state.schedules.isNotEmpty() -> items(state.schedules) { ScheduleListItem(it) }
                state.tasks.isNotEmpty() -> items(state.tasks) { TaskListItem(it) }
            }
        }
    }
}

