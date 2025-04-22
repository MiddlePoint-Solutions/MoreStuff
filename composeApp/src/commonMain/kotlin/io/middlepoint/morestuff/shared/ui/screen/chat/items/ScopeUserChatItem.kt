package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.domain.enums.MessageType
import io.middlepoint.morestuff.shared.ui.local.LocalUserInteractionEnabled
import io.middlepoint.morestuff.shared.ui.model.IAMessageUiModel
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.model.isDataMessage
import io.middlepoint.morestuff.shared.ui.model.isPdfMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import io.middlepoint.morestuff.shared.ui.screen.chat.items.MockData.messageUiModel
import io.middlepoint.morestuff.shared.ui.screen.schedule.iaScope.ScopeChatActions
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScopeUserChatItem(
  message: IAMessageUiModel,
  actions: ScopeChatActions,
) {
  val isNewUserTask by remember {
    derivedStateOf { message.contentType == ContentType.USER_NEW_TASK }
  }

  var showMenu by remember { mutableStateOf(false) }

  val isEditing by remember(message.id) {
    derivedStateOf { actions.isMessageBeingEdited(message.id) }
  }

  val messageType by remember {
    derivedStateOf {
      when {
        message.isPdfMessage -> MessageType.Pdf
        message.isDataMessage -> MessageType.Image
        else -> MessageType.Text
      }
    }
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 45.dp, end = 5.dp, bottom = 4.dp),
    horizontalArrangement = Arrangement.End
  ) {

    Surface(
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomEnd = 5.dp,
        bottomStart = 14.dp
      ),
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier
        .combinedClickable(
          enabled = LocalUserInteractionEnabled.current && !isEditing,
          onClick = {
            when (messageType) {
              MessageType.Text -> showMenu = true
              MessageType.Image -> actions.onImageSelected(message)
              MessageType.Pdf -> actions.onPdfSelected(message)
            }
          },
          onLongClick = { showMenu = true }
        )
    ) {
      //TODO: think about this:
     /* if (!isEditing) {
        UserMessageContextMenu(
          message,
          showMenu = showMenu,
          actions = actions,
          onDismissRequest = { showMenu = false },
        )
      }*/

      when (messageType) {
        MessageType.Pdf -> {
          message.messageData?.filePath?.let { filePath ->
            ScopePDFMessageItem(
              pdfPath = filePath,
              message = message,
            )
          }
        }

        MessageType.Image -> {
          ScopeImageMessageItem(message = message)
        }

        else -> {
          ScopeTextMessageItem(
            message = message,
            onNonLinkClick = { showMenu = true },
          )
        }
      }
    }
  }
}





