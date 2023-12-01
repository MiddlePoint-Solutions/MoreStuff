package co.softov.morestuff.android.ui.scope

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
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
import co.softov.morestuff.android.ui.home.HomeUiEvent
import co.softov.morestuff.android.ui.home.HomeViewModel
import co.softov.morestuff.android.ui.schedule.PriorityContent
import co.softov.morestuff.android.ui.theme.surfaceContainer
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber


@Composable
fun ScopesScreen(
    showTaskChat: (taskId: Long) -> Unit,
) {

    val homeViewModel: HomeViewModel = koinViewModel()
    val scopeState = homeViewModel.uiState.collectAsState()
    val tasks by homeViewModel.tasks.collectAsState()
    val model by homeViewModel.uiModel.collectAsState()
    var taskOptions by remember { mutableLongStateOf(0) }
    val priorityScrollState = rememberLazyListState()

    LaunchedEffect(tasks.size) {
        priorityScrollState.scrollToItem(0)
    }

    if (scopeState.value.scopes.isNotEmpty()) {
        Column {
            scopeState.value.selectedScopeId?.let { selectedScopeId ->
                val selectedScope = scopeState.value.scopes.find { it.scopeId == selectedScopeId }
                selectedScope?.let {
                    ScopeTabs(
                        scopes = scopeState.value.scopes,
                        selectedScope = it,
                        onScopeSelected = { scope ->
                            homeViewModel.handleEvent(
                                HomeUiEvent.SelectScope(
                                    scope.scopeId
                                )
                            )
                        },
                    )
                }
            }
            Timber.d("Selected Scope ID: ${scopeState.value.selectedScopeId}")
            Timber.d("Scopes: ${scopeState.value.scopes}")

            Spacer(Modifier.height(8.dp))

            Crossfade(
                targetState = scopeState.value.selectedScopeId,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), label = ""
            ) { scopeId ->
                scopeId?.let {
                    PriorityContent(
                        tasks = tasks,
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
            }
        }
    }
}

@Composable
fun ScopeTabs(
    scopes: List<ScopeDomain>,
    selectedScope: ScopeDomain,
    onScopeSelected: (ScopeDomain) -> Unit,
) {
    val selectedIndex = scopes.indexOfFirst { it == selectedScope }
    val selectedTabColor = MaterialTheme.colorScheme.primary
    val unselectedTabColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        indicator = { tabPositions ->
            CustomIndicator(
                Modifier.tabIndicatorOffset(
                    currentTabPosition = tabPositions[selectedIndex],
                    indicatorWidth = 20.dp
                ),
                color = selectedTabColor,
                height = 3.dp,
                cornerRadius = 8.dp
            )
        },
    ) {
        scopes.forEachIndexed { index, scope ->
            Timber.d("Creating tab for scope: ${scope.name}")
            Tab(
                selected = index == selectedIndex,
                onClick = { onScopeSelected(scope) },
                text = {
                    Text(
                        text = scope.name,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight(500),
                        ),
                        color = if (index == selectedIndex) selectedTabColor else unselectedTabColor
                    )
                }
            )
        }
    }
}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition,
    indicatorWidth: Dp = 17.dp
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
    cornerRadius: Dp = 10.dp
) {
    Box(
        modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(color = color)
    )
}

