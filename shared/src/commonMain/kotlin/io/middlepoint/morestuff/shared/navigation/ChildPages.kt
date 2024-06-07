package io.middlepoint.morestuff.shared.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.KSerializer

@OptIn(ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)
@Composable
inline fun <reified C : Any> ChildPages(
    source: PagesNavigation<C>,
    serializer: KSerializer<C>?,
    noinline initialPages: () -> Pages<C>,
    modifier: Modifier = Modifier,
    handleBackButton: Boolean = true,
    noinline content: @Composable (C) -> Unit,
) {
    val componentContext = LocalComponentContext.current

    Pages(
        pages = remember {
            componentContext.childPages(
                source = source,
                serializer = serializer,
                initialPages = initialPages,
                handleBackButton = handleBackButton,
                childFactory = { _, childComponentContext -> childComponentContext },
            )
        } ,
        modifier = modifier,
        onPageSelected = { },
        pager = { pageCount, m, state, key, pageContent ->
            HorizontalPager(
                state = state,
                modifier = m,
                key = key,
                userScrollEnabled = false,
                pageContent = pageContent,
            )
        },
        scrollAnimation = PagesScrollAnimation.Default,
    ) { index, page ->
//        ProvideComponentContext(page) {
//            content(initialPages().items[index])
//        }
    }
}

/**
 * Displays a list of pages represented by [ChildPages].
 */
@ExperimentalFoundationApi
@ExperimentalDecomposeApi
@Composable
fun <T : Any> Pages(
    pages: Value<ChildPages<*, T>>,
    onPageSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollAnimation: PagesScrollAnimation = PagesScrollAnimation.Disabled,
    pager: Pager = defaultHorizontalPager(),
    pageContent: @Composable (index: Int, page: T) -> Unit,
) {
    val state = pages.subscribeAsState()

    Pages(
        pages = state.value,
        onPageSelected = onPageSelected,
        modifier = modifier,
        scrollAnimation = scrollAnimation,
        pager = pager,
        pageContent = pageContent,
    )
}

/**
 * Displays a list of pages represented by [ChildPages].
 */
@ExperimentalFoundationApi
@ExperimentalDecomposeApi
@Composable
private fun <T : Any> Pages(
    pages: ChildPages<*, T>,
    onPageSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollAnimation: PagesScrollAnimation = PagesScrollAnimation.Disabled,
    pager: Pager = defaultHorizontalPager(),
    pageContent: @Composable (index: Int, page: T) -> Unit,
) {
    val selectedIndex = pages.selectedIndex
    val state = rememberPagerState(initialPage = selectedIndex) { pages.items.size }

    LaunchedEffect(selectedIndex) {
        if (state.currentPage != selectedIndex) {
            when (scrollAnimation) {
                is PagesScrollAnimation.Disabled -> state.scrollToPage(selectedIndex)
                is PagesScrollAnimation.Default -> state.animateScrollToPage(page = selectedIndex)
                is PagesScrollAnimation.Custom -> state.animateScrollToPage(
                    page = selectedIndex,
                    animationSpec = scrollAnimation.spec
                )
            }
        }
    }

    DisposableEffect(state.settledPage) {
        onPageSelected(state.settledPage)
        onDispose {}
    }

    val items = pages.items

    pager(
        items.size,
        modifier,
        state,
        { items[it].configuration },
    ) { pageIndex: Int ->
        items[pageIndex].instance?.also { page ->
            pageContent(pageIndex, page)
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalDecomposeApi
fun defaultHorizontalPager(): Pager =
    { pageCount, modifier, state, key, pageContent ->
        HorizontalPager(
            modifier = modifier,
            state = state,
            key = key,
            pageContent = pageContent,
        )
    }

@OptIn(ExperimentalFoundationApi::class)
internal typealias Pager =
        @Composable (
            pageCount: Int,
            Modifier,
            PagerState,
            key: (index: Int) -> Any,
            pageContent: @Composable PagerScope.(page: Int) -> Unit,
        ) -> Unit