package co.softov.morestuff.android.presentation.list

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.fragment.BaseBottomSheetDialogFragment
import co.softov.morestuff.android.presentation.list.schedule.all.ScheduleList
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme
import com.google.accompanist.pager.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ListsFragment : BaseBottomSheetDialogFragment() {

    override val layoutResourceId: Int = R.layout.fragment_tasks_pager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val height = getWindowHeight()
        val view = LayoutInflater.from(context).inflate(layoutResourceId, null)
        dialog.setContentView(
            view,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        )

        val bottomSheetDialog = dialog as BottomSheetDialog
        bottomSheetDialog.behavior.isFitToContents = false
        bottomSheetDialog.behavior.peekHeight = height / 2
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MoreStuffTheme {
                    ListsContent()
                }
            }
        }
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = requireActivity().resources.displayMetrics
        return displayMetrics.heightPixels
    }

}

val pages = listOf(
    PageType.PAGE_ACTIVE_SCHEDULES,
    PageType.PAGE_ACTIVE_TODAY,
    PageType.PAGE_ACTIVE_TOMORROW,
    PageType.PAGE_ACTIVE_LATER,
    PageType.PAGE_COMPLETE_TASKS,
    PageType.PAGE_ACTIVE_TASKS
).map { Page(it) }

data class Page(val type: PageType)

val Page.title
    @Composable get() = when (this.type) {
        PageType.PAGE_ACTIVE_TASKS -> stringResource(id = R.string.page_title_active)
        PageType.PAGE_COMPLETE_TASKS -> stringResource(id = R.string.page_title_complete)
        PageType.PAGE_ACTIVE_SCHEDULES -> stringResource(id = R.string.page_title_schedule)
        PageType.PAGE_ACTIVE_TODAY -> stringResource(id = R.string.page_title_today)
        PageType.PAGE_ACTIVE_TOMORROW -> stringResource(id = R.string.page_title_tomorrow)
        PageType.PAGE_ACTIVE_LATER -> stringResource(id = R.string.page_title_later)
    }

enum class PageType {
    PAGE_ACTIVE_TASKS,
    PAGE_COMPLETE_TASKS,
    PAGE_ACTIVE_SCHEDULES,
    PAGE_ACTIVE_TODAY,
    PAGE_ACTIVE_TOMORROW,
    PAGE_ACTIVE_LATER
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ListsContent(
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = pages.size)
    Column(modifier) {
        TaskListTabs(pagerState, pages)

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
private fun TaskListTabs(
    pagerState: PagerState,
    pages: List<Page>
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
