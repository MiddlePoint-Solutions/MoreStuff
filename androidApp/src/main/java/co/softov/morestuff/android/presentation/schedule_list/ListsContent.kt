package co.softov.morestuff.android.presentation.schedule_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.presentation.schedule_list.model.PageType
import co.softov.morestuff.android.presentation.schedule_list.model.title
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

val pages = listOf(
    PageType.PAGE_ACTIVE_SCHEDULES,
    PageType.PAGE_ACTIVE_TODAY,
    PageType.PAGE_ACTIVE_TOMORROW,
    PageType.PAGE_ACTIVE_LATER,
    PageType.PAGE_COMPLETE_TASKS,
    PageType.PAGE_ACTIVE_TASKS
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ListsContent(
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = pages.size)
    Column(modifier) {
        ScheduleTabs(pagerState, pages)

        HorizontalPager(state = pagerState) { page ->
            Surface(
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.primary)
            ) {
                ScheduleList(page = pages[page])
            }

        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ScheduleTabs(
    pagerState: PagerState,
    pages: List<PageType>
) {
    val coroutineScope = rememberCoroutineScope()
    ScrollableTabRow(
        // Our selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 24.dp,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
            )
        },
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
        ListsContent()
    }
}