package co.softov.morestuff.android.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.model.PageType
import co.softov.morestuff.android.ui.model.title
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import kotlinx.coroutines.launch

val pages = listOf(
    PageType.PAGE_ACTIVE_NOW,
    PageType.PAGE_ACTIVE_LATER,
    PageType.PAGE_COMPLETE_TASKS,
    PageType.PAGE_SCHEDULE
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListsContent(
    modifier: Modifier = Modifier,
    itemAction: (taskId: Long) -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        pages.size
    }
    Column(modifier) {
        ScheduleTabs(pagerState, pages)
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            pageSpacing = 0.dp,
            userScrollEnabled = true,
            reverseLayout = false,
            contentPadding = PaddingValues(0.dp),
            beyondBoundsPageCount = 0,
            pageSize = PageSize.Fill,
            key = null,
            pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
                Orientation.Horizontal
            ),
            pageContent = { page ->
                Surface(
                    contentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.primary)
                ) {
                    ScheduleList(page = pages[page], itemAction)
                }
            }

        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleTabs(
    pagerState: PagerState,
    pages: List<PageType>
) {
    val coroutineScope = rememberCoroutineScope()
    ScrollableTabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 3.dp,
        divider = {},
        modifier = Modifier.fillMaxWidth()
    ) {
        pages.forEachIndexed { index, page ->
            Tab(
                text = { Text(page.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.scrollToPage(index) }
                }
            )
        }
    }
}

@Preview
@Composable
fun ListsContentPreview() {
    MoreStuffTheme {
        ListsContent() {

        }
    }
}