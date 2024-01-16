package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.ScopeDomain

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScopeCarousel(
    scopes: List<ScopeDomain>,
    currentScope: Long,
    onScopeSelected: (Long) -> Unit,
    initialIndex: Int,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val itemWidth = screenWidth / 3
        val halfItemWidth = itemWidth / 2
        val centerPadding = screenWidth / 2 - halfItemWidth

        val page = currentScope.let { id ->
            scopes.indexOfFirst { it.id == id }.coerceAtLeast(0)
        }

        val pagerState = rememberPagerState(
            initialPage = initialIndex,
            pageCount = { scopes.size }
        )

        LaunchedEffect(currentScope) {
            pagerState.animateScrollToPage(page)
        }


        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            contentPadding = PaddingValues(
                start = centerPadding,
                end = centerPadding
            )
        ) { page ->
            val scope = scopes.getOrNull(page)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                if (scope != null) {
                    ScopeCarouselItem(
                        scopeId = scope.id,
                        scopeName = scope.name,
                        onSelectScope = onScopeSelected,
                        isSelected = page == pagerState.currentPage,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxHeight()
                            .width(itemWidth),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScopeCarouselItem(
    scopeId: Long,
    scopeName: String,
    onSelectScope: (Long) -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isSelected) {
        if (isSelected) {
            onSelectScope(scopeId)
        }
    }
    Column(
        modifier = modifier
            .clickable {
                onSelectScope(scopeId)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = scopeName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isSelected) 20.sp else 15.sp,
            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondary.copy(
                alpha = 0.2f
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}