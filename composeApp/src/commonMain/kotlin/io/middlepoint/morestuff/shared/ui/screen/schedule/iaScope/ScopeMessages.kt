package io.middlepoint.morestuff.shared.ui.screen.schedule.iaScope

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import io.middlepoint.morestuff.shared.domain.service.logger
import io.middlepoint.morestuff.shared.ui.local.ProvideUserInteractionEnabled
import io.middlepoint.morestuff.shared.ui.model.IAMessageUiModel
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel

import io.middlepoint.morestuff.shared.ui.screen.chat.JumpToBottom
import io.middlepoint.morestuff.shared.ui.screen.chat.isAutoScrollingEnabled
import io.middlepoint.morestuff.shared.ui.screen.chat.items.ScopeAppChatItem
import io.middlepoint.morestuff.shared.ui.screen.chat.items.ScopeUserChatItem
import kotlinx.coroutines.launch


private val jumpToBottomThreshold = 150.dp

@Composable
fun ScopeMessages(
  messages: List<IAMessageUiModel>,
  scrollState: LazyListState = rememberLazyListState(),
  modifier: Modifier = Modifier,
  actions: ScopeChatActions = ScopeChatActions(),
  userInteractionEnabled: Boolean = true,
  contentPadding: PaddingValues = PaddingValues(0.dp),
) {
  val scope = rememberCoroutineScope()
  var itemsCount by remember { mutableIntStateOf(0) }
  val enableAutoScroll = isAutoScrollingEnabled(messages.size, itemsCount, scrollState)
  itemsCount = messages.size

  LaunchedEffect(messages) {
    logger.d { "Messages in Scope Messages component: ${messages.size}" }
    messages.forEach { message ->
      logger.d { " Scope Message in component: id=${message.id}, type=${message.contentType}, content='${message.content}'" }
    }
  }

  ProvideUserInteractionEnabled(userInteractionEnabled) {

    Box(modifier = modifier) {

      LazyColumn(
        reverseLayout = true,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = userInteractionEnabled,
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

          Column(modifier.padding(top = 8.dp)) {
            if (isLastMessageOfDay) {
              DateTimeItem(item)
            }

            when (item.contentType) {
              ContentType.TASK_MESSAGE,
              ContentType.USER_NEW_TASK -> ScopeUserChatItem(
                message = item,
                actions = actions,
              )

              ContentType.CONFIRM_NEW_TASK,
              ContentType.APP_TASK_MESSAGE -> ScopeAppChatItem(item, actions)
              ContentType.AI_TASK_MESSAGE -> {
                logger.d { "Rendering message: ${item.contentType}, ID=${item.id}" }
                ScopeAppChatItem(item, actions)
              }
              ContentType.TASK_REMINDER -> {}
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
}


@Composable
private fun DateTimeItem(item: IAMessageUiModel) {
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