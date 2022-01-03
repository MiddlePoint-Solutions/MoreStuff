package co.softov.morestuff.android.ui.main

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.domain.model.Message
import kotlinx.coroutines.launch

private val JumpToBottomThreshold = 56.dp

private fun isAutoScrollingEnabled(
    pagingItemsCount: Int,
    itemsCount: Int,
    scrollState: LazyListState
) = (pagingItemsCount > itemsCount) && scrollState.firstVisibleItemIndex == 0

@Composable
fun Messages(
    messages: List<Message>,
    actions: ChatActions,
    modifier: Modifier = Modifier,
    scrollState: LazyListState
) {

    val scope = rememberCoroutineScope()
    val itemsCount = remember { mutableStateOf(0) }
    val enableAutoScroll = isAutoScrollingEnabled(messages.size, itemsCount.value, scrollState)
    itemsCount.value = messages.size

    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier.fillMaxSize(),
            state = scrollState,
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(
                items = messages,
                key = { item -> item.id }
            ) { item ->
                when (item.contentType) {
                    ContentType.USER_NEW_TASK -> UserChatItem(message = item)
                    ContentType.CONFIRM_NEW_TASK -> AppChatItem(message = item)
                    ContentType.TASK_REMINDER -> TaskReminderItem(message = item, actions = actions)
                }
            }
        }

        if (enableAutoScroll) {
            scope.launch {
                scrollState.scrollToItem(0)
            }
        }

        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private enum class Visibility {
    VISIBLE,
    GONE
}

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Show Jump to Bottom button
    val transition = updateTransition(
        if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = "JumpToBottomTransition"
    )
    val bottomOffset by transition.animateDp(label = "JumpToBottomTransition") {
        if (it == Visibility.GONE) {
            (-32).dp
        } else {
            32.dp
        }
    }
    if (bottomOffset > 0.dp) {
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    modifier = Modifier.height(18.dp),
                    contentDescription = null
                )
            },
            text = {
                Text(text = stringResource(id = co.softov.morestuff.android.R.string.new_messages))
            },
            onClick = onClicked,
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.primary,
            modifier = modifier
                .offset(x = 0.dp, y = -bottomOffset)
                .height(36.dp)
        )
    }
}

