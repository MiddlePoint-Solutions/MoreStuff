package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.KmpFile
import io.middlepoint.morestuff.shared.domain.enums.ContentType
import io.middlepoint.morestuff.shared.ui.extension.getFileName
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import io.middlepoint.morestuff.shared.ui.screen.chat.items.MockData.messageUiModel
import io.middlepoint.morestuff.shared.ui.theme.MoreStuffTheme
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.error
import morestuff.composeapp.generated.resources.filetype_pdf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UserChatItem(
  message: MessageUiModel,
  actions: ChatActions,
) {
  var showMenu by remember { mutableStateOf(false) }
  val isNewUserTask by remember {
    derivedStateOf { message.contentType == ContentType.USER_NEW_TASK }
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 45.dp, end = 5.dp, bottom = 4.dp),
    horizontalArrangement = Arrangement.End
  ) {
    Box {
      Row(
        modifier = Modifier.align(Alignment.CenterEnd)
      ) {

        if (isNewUserTask) {
          FilledIconButton(onClick = { actions.taskChatAction(message.taskId) }) {
            Icon(
              imageVector = Icons.Default.Start,
              contentDescription = ""
            )
          }
        }

        MessageContent(
          message = message,
          actions = actions,
          showMenu = { showMenu = true }
        )
      }

      ShowContextMenu(
        message,
        showMenu = showMenu,
        actions = actions,
        close = { showMenu = false },
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageContent(
  message: MessageUiModel,
  actions: ChatActions,
  showMenu: () -> Unit
) {
  when {
    message.isPdfMessage -> {
      message.messageData?.filePath?.let { filePath ->
        PDFMessageItem(
          pdfPath = filePath,
          message = message,
          modifier = Modifier.combinedClickable(
            onClick = { actions.onPdfSelected(message) },
            onLongClick = showMenu
          )
        )
      }
    }

    message.isDataMessage -> {
      ImageMessage(
        message = message,
        actions = actions,
        showMenu = showMenu,
      )
    }

    else -> {
      MessageText(
        message = message,
        showMenu = showMenu
      )
    }
  }
}

@Composable
fun MessageTime(
  formattedTimeOnly: String?,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
  formattedTimeOnly?.let {
    Text(
      text = it,
      style = MaterialTheme.typography.bodySmall.copy(color = textColor),
      modifier = modifier.padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
    )
  }
}

@Preview
@Composable
fun UserChatItemPreview() {
  MoreStuffTheme {
    UserChatItem(messageUiModel, ChatActions())
  }
}

@Composable
private fun PDFMessageItem(
  pdfPath: String,
  message: MessageUiModel,
  modifier: Modifier = Modifier,
) {
  val pdfFile = remember(pdfPath) { KmpFile.createKmpFile(pdfPath) }
  val platformContext = LocalPlatformContext.current

  Surface(
    shape = RoundedCornerShape(
      topStart = 14.dp,
      topEnd = 14.dp,
      bottomEnd = 5.dp,
      bottomStart = 14.dp
    ),
    color = MaterialTheme.colorScheme.primary,
    modifier = modifier.padding(2.dp)
  ) {
    Box {
      Row(
        modifier = Modifier.fillMaxWidth()
      ) {
        BoxWithConstraints(modifier = Modifier.weight(0.8f)) {
          val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
          PDFPagePreview(
            pdfFile = pdfFile,
            width = width,
            height = width,
          )
        }

        Column(
          modifier = Modifier
            .padding(8.dp)
            .weight(2f)
        ) {
          Text(
            text = pdfFile.getFileName(platformContext) ?: stringResource(Res.string.error),
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
          )

          Text(
            text = stringResource(Res.string.filetype_pdf),
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.outlineVariant
            )
          )
        }
      }

      MessageTime(
        formattedTimeOnly = message.formattedTimeOnly,
        modifier = Modifier.align(Alignment.BottomEnd)
      )
    }
  }
}

