package co.softov.morestuff.android.presentation.list

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.app.presentation.fragment.BaseBottomSheetDialogFragment
import co.softov.morestuff.android.presentation.list.schedule.all.AllScheduleList
import co.softov.morestuff.android.presentation.theme.MoreStuffTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
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
                    ListsContent(pages)
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

private val pages = listOf(
    Pages.PAGE_ACTIVE_SCHEDULES,
    Pages.PAGE_ACTIVE_TODAY,
    Pages.PAGE_ACTIVE_TOMORROW,
    Pages.PAGE_ACTIVE_LATER,
    Pages.PAGE_COMPLETE_TASKS,
    Pages.PAGE_ACTIVE_TASKS
)


enum class Pages(val title: String) {
    PAGE_ACTIVE_TASKS("Active"),
    PAGE_COMPLETE_TASKS("Complete"),
    PAGE_ACTIVE_SCHEDULES("Schedule"),
    PAGE_ACTIVE_TODAY("Today"),
    PAGE_ACTIVE_TOMORROW("Tomorrow"),
    PAGE_ACTIVE_LATER("Later")
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ListsContent(
    pages: List<Pages>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = pages.size)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier) {
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
                        coroutineScope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            Surface(
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.primary)
            ) {
                AllScheduleList()
            }

        }
    }
}

@Preview
@Composable
fun ListsContentPreview() {
    MoreStuffTheme {
        ListsContent(pages = pages)
    }
}
