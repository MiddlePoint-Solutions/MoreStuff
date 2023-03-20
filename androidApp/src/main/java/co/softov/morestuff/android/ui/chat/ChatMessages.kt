package co.softov.morestuff.android.ui.chat

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import co.softov.morestuff.android.ui.chat.items.AppChatItem
import co.softov.morestuff.android.ui.chat.items.TaskReminderItem
import co.softov.morestuff.android.ui.chat.items.UserChatItem
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
    var itemsCount by remember { mutableStateOf(0) }
    val enableAutoScroll = isAutoScrollingEnabled(messages.size, itemsCount, scrollState)
    itemsCount = messages.size

    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            state = scrollState,
            contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
        ) {
            items(
                items = messages,
                key = { item -> item.id }
            ) { item ->

                when (item.contentType) {
                    ContentType.USER_NEW_TASK -> UserChatItem(message = item, actions)
                    ContentType.CONFIRM_NEW_TASK -> AppChatItem(message = item, actions)
                    ContentType.TASK_REMINDER -> TaskReminderItem(message = item, actions = actions)
                }
            }
        }

        if (enableAutoScroll) {
            LaunchedEffect(key1 = itemsCount) {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
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
                (scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold)
            }
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.scrollToItem(0)
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
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .offset(x = 0.dp, y = -bottomOffset)
                .height(36.dp)
        )
    }
}

