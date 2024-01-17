package co.softov.morestuff.android.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.ScopeDomain
import timber.log.Timber

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
        var isSelected by remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { scopes.size })

        // Animar al HorizontalPager cuando el currentScope cambia externamente
        LaunchedEffect(currentScope) {
            val targetPage = scopes.indexOfFirst { it.id == currentScope }.coerceAtLeast(0)
            pagerState.animateScrollToPage(targetPage)
        }

        // Observar cambios en la página actual del HorizontalPager
        LaunchedEffect(pagerState.currentPage) {
            val selectedScopeId = scopes.getOrNull(pagerState.currentPage)?.id
            if (selectedScopeId != null && selectedScopeId != currentScope) {
                onScopeSelected(selectedScopeId)
            }
        }


        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            contentPadding = PaddingValues(start = centerPadding, end = centerPadding)
        ) { page ->
            scopes.getOrNull(page)?.let { scope ->
                ScopeCarouselItem(
                    scopeId = scope.id,
                    scopeName = scope.name,
                    onSelectScope = onScopeSelected,
                    isSelected = page == pagerState.currentPage,
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxHeight()
                        .width(itemWidth),
                )
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
    val alphaAnimatable = remember { Animatable(if (isSelected) 1f else 0.2f) }

    LaunchedEffect(isSelected) {
        alphaAnimatable.animateTo(
            targetValue = if (isSelected) 1f else 0.2f,
            animationSpec = tween(durationMillis = 500)
        )
    }
    /*LaunchedEffect(isSelected) {
        if (isSelected) {
            onSelectScope(scopeId)
        }
    }*/

    Column(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    onSelectScope(scopeId)
                }
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = scopeName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isSelected) 20.sp else 15.sp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.secondary.copy(alpha = alphaAnimatable.value)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


/*LaunchedEffect(currentScope, isSelected) {
    val targetPage = scopes.indexOfFirst { it.id == currentScope }.coerceAtLeast(0)
    pagerState.animateScrollToPage(targetPage)

    if (isSelected) {
        onScopeSelected(currentScope)
    }
}*/


/* val currentPage = currentScope.let { id ->
     scopes.indexOfFirst { it.id == id }.coerceAtLeast(0)
 }
 LaunchedEffect(currentScope) {
     pagerState.animateScrollToPage(currentPage)
 }*/

/* HorizontalPager(
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
                         .padding(horizontal = 2.dp)
                 ) {
                     if (scope != null) {
                         ScopeCarouselItem(
                             scopeId = scope.id,
                             scopeName = scope.name,
                             onSelectScope = onScopeSelected,
                             isSelected = page == pagerState.currentPage,
                             modifier = Modifier
                                 .padding(2.dp)
                                 .fillMaxHeight()
                                 .width(itemWidth),
                         )
                     }
                 }
             }*/