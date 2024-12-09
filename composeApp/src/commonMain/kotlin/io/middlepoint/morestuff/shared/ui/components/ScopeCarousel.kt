package io.middlepoint.morestuff.shared.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.domain.model.ScopeDomain
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun ScopeCarousel(
  scopes: List<ScopeDomain>,
  currentScopeId: Long,
  onScopeSelected: (Long) -> Unit,
  modifier: Modifier = Modifier,
  pagerState: PagerState = rememberPagerState(
    initialPage = scopes.indexOfFirst { it.id == currentScopeId },
    pageCount = { scopes.size }
  )
) {

  if (scopes.isEmpty()) {
    return
  }

  val coroutineScope = rememberCoroutineScope()

  BoxWithConstraints {
    val screenWidth = maxWidth
    val itemWidth = (screenWidth.value / 3.5).dp
    val halfItemWidth = itemWidth / 2
    val centerPadding = ((screenWidth / 2) - halfItemWidth) + 20.dp

    LaunchedEffect(Unit) {
      snapshotFlow { pagerState.currentPage }
        .debounce(500)
        .collect {
          onScopeSelected(scopes[it].id)
        }
    }

    HorizontalPager(
      state = pagerState,
      modifier = modifier,
      contentPadding = PaddingValues(start = centerPadding, end = centerPadding),
    ) { page ->
      val scope = scopes[page]
      ScopeCarouselItem(
        scope = scope,
        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(page) } },
        isSelected = page == pagerState.currentPage,
        modifier = Modifier
          .padding(2.dp)
          .widthIn(min = itemWidth, max = itemWidth + 50.dp),
      )
    }
  }
}

@Composable
private fun ScopeCarouselItem(
  scope: ScopeDomain,
  onClick: () -> Unit,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
) {

  val currentState by remember(isSelected) { mutableStateOf(isSelected) }
  val transition = updateTransition(currentState, label = "scope item state")

  val alphaState by transition.animateFloat(label = "text alpha") { state ->
    when (state) {
      true -> 1f
      false -> 0.2f
    }
  }

  val fontState by transition.animateInt(label = "text font") { state ->
    when (state) {
      false -> 15
      true -> 20
    }
  }

  Column(
    modifier = modifier.clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = null,
      onClick = onClick
    ),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = scope.name,
      modifier = Modifier.graphicsLayer { alpha = alphaState },
      style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 16.sp),
      fontSize = fontState.sp,
      color = MaterialTheme.colorScheme.secondary,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )
  }
}
