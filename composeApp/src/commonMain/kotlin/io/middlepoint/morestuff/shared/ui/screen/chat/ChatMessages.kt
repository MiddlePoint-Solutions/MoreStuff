package io.middlepoint.morestuff.shared.ui.screen.chat

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.items.AppChatItem
import io.middlepoint.morestuff.shared.ui.screen.chat.items.UserMessageContextMenu
import io.middlepoint.morestuff.shared.ui.screen.chat.items.TaskReminderItem
import io.middlepoint.morestuff.shared.ui.screen.chat.items.UserChatItem
import kotlinx.coroutines.launch
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.new_messages
import org.jetbrains.compose.resources.stringResource

private val jumpToBottomThreshold = 150.dp

private fun isAutoScrollingEnabled(
    pagingItemsCount: Int,
    itemsCount: Int,
    scrollState: LazyListState,
) = (pagingItemsCount > itemsCount) && scrollState.firstVisibleItemIndex == 0

@Composable
fun Messages(
    messages: List<MessageUiModel>,
    scrollState: LazyListState,
    actions: ChatActions = ChatActions(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val scope = rememberCoroutineScope()
    var itemsCount by remember { mutableIntStateOf(0) }
    val enableAutoScroll = isAutoScrollingEnabled(messages.size, itemsCount, scrollState)
    itemsCount = messages.size

    Box(modifier = modifier) {

        LazyColumn(
            reverseLayout = true,
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            state = scrollState,
            contentPadding = contentPadding
        ) {
            itemsIndexed(
                items = messages,
                key = { _, item -> item.id },
                contentType = { _, item -> item.contentType }
            ) { index, item ->

                val nextMessage = messages.getOrNull(index + 1)
                val isLastMessageOfDay = remember(nextMessage) {
                    item.formattedTime != nextMessage?.formattedTime
                }

                Column {
                    if (isLastMessageOfDay) {
                        DateTimeItem(item)
                    }

                    when (item.contentType) {
                        ContentType.TASK_MESSAGE,
                        ContentType.USER_NEW_TASK -> UserChatItem(item, actions)

                        ContentType.CONFIRM_NEW_TASK,
                        ContentType.APP_TASK_MESSAGE -> AppChatItem(item, actions)

                        ContentType.TASK_REMINDER -> TaskReminderItem(item, actions)

                    }
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
            jumpToBottomThreshold.toPx()
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

@Composable
private fun DateTimeItem(item: MessageUiModel) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = item.formattedTime,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07F),
                    shape = RoundedCornerShape(38)
                )
                .padding(
                    start = 35.dp,
                    end = 35.dp,
                    bottom = 5.dp,
                    top = 5.dp
                ),
            fontSize = 14.sp,
            color = Color(0xFF5C5E74),
            textAlign = TextAlign.Center
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
    modifier: Modifier = Modifier,
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
                Text(text = stringResource(Res.string.new_messages))
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

