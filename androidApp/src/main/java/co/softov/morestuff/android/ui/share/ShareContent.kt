package co.softov.morestuff.android.ui.share

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.schedule.PriorityItem
import co.softov.morestuff.android.ui.schedule.PriorityViewModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShareContent(
    shareToTask: (taskId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PriorityViewModel = koinViewModel(),
) {

    val state = rememberLazyListState()

    Box(modifier) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {
            items(viewModel.tasks, key = { it.id }) { task ->
                PriorityItem(
                    task = task,
                    onClick = shareToTask
                )

                Divider(
                    thickness = 0.8.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
fun ShareContentPreview() {
    MoreStuffTheme(darkTheme = true) {
        ShareContent(shareToTask = {})
    }
}