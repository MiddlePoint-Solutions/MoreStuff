package co.softov.morestuff.android.ui.scope

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.ScopeDomain
import co.softov.morestuff.android.ui.home.HomeViewModel
import co.softov.morestuff.android.ui.schedule.PriorityContent
import co.softov.morestuff.android.ui.schedule.ScopeViewModel
import co.softov.morestuff.android.ui.theme.surfaceContainer
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScopesScreen(
    showTaskChat: (taskId: Long) -> Unit,
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val model by homeViewModel.uiModel.collectAsState()
    var taskOptions by remember { mutableLongStateOf(0L) }
    val priorityScrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val scopes = remember(model.scopes) { model.scopes }
    val pagerState = rememberPagerState(pageCount = { scopes.size })

    if (scopes.isNotEmpty()) {
        Column {
            ScopeTabs(
                currentPage = pagerState.currentPage,
                scopes = scopes,
                onScopeSelected = { index, scope ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )

            Divider(
                thickness = Dp.Hairline
            )

            Spacer(Modifier.height(8.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                pageContent = {

                    val scopeViewModel = koinViewModel<ScopeViewModel>(
                        key = "Scope$it",
                        parameters = { parametersOf(scopes[it].scopeId) }
                    )

                    val scopeTasks by scopeViewModel.scopeTasks.collectAsState()

                    PriorityContent(
                        tasks = scopeTasks,
                        onItemClick = { taskId ->
                            if (model.taskSelectionActive) {
                                homeViewModel.toggleTaskSelection(taskId)
                            } else {
                                showTaskChat(taskId)
                            }
                        },
                        onItemLongClick = homeViewModel::toggleTaskSelection,
                        showTaskOptions = { taskOptions = it },
                        toggleQuickReminder = homeViewModel::toggleQuickReminder,
                        listState = priorityScrollState,
                        taskSelectionActive = { model.taskSelectionActive },
                        modifier = Modifier.padding(bottom = 20.dp),
                    )
                }
            )
        }
    }
}


@Composable
fun ScopeTabs(
    currentPage: Int,
    scopes: List<ScopeDomain>,
    onScopeSelected: (index: Int, scope: ScopeDomain) -> Unit,
) {
    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    ScrollableTabRow(
        selectedTabIndex = currentPage,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        indicator = { tabPositions ->
            CustomIndicator(
                Modifier.tabIndicatorOffset(
                    currentTabPosition = tabPositions[currentPage],
                ),
                color = selectedTabColor,
                height = 3.dp,
                cornerRadius = 8.dp
            )
        },
        divider = {}
    ) {
        scopes.forEachIndexed { index, scope ->
            Timber.d("Creating tab for scope: ${scope.name}")
            Tab(
                selected = index == currentPage,
                onClick = { onScopeSelected(index, scope) },
                text = {
                    Text(
                        text = scope.name,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(500),
                        ),
                        color = if (index == currentPage) selectedTabColor else unselectedTabColor
                    )
                }
            )
        }
    }
}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    indicatorWidth: Dp = 30.dp,
): Modifier = composed {
    val tabCenter = currentTabPosition.left + currentTabPosition.width / 2

    val indicatorOffset = tabCenter - indicatorWidth / 2

    val animatedOffset by animateDpAsState(
        targetValue = indicatorOffset,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing), label = ""
    )

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = animatedOffset)
        .width(indicatorWidth)
}

@Composable
fun CustomIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    width: Dp = 17.dp,
    height: Dp = 3.dp,
    cornerRadius: Dp = 10.dp,
) {
    Box(
        modifier
            //.width(width)
            .height(height)
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(color = color)
    )
}

