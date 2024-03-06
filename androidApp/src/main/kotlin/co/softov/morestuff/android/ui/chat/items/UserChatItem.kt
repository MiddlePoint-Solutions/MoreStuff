package co.softov.morestuff.android.ui.chat.items

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.items.MockData.messageUiModel
import co.softov.morestuff.android.ui.model.MessageUiModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.utils.appendUrlsWithStyle
import co.softov.morestuff.android.ui.utils.urlPattern
import kotlinx.coroutines.launch


@Composable
fun UserChatItem(
    message: MessageUiModel,
    actions: ChatActions,
) {
    var showMenu by remember { mutableStateOf(false) }
    val isNewUserTask by remember {
        derivedStateOf { message.contentType == ContentType.USER_NEW_TASK }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.padding(start = 45.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 5.dp, bottom = 4.dp)
                    .clickable { showMenu = true }
            ) {
                MessageContent(
                    message = message,
                    actions = actions,
                    showMenu = { showMenu = true },
                )
                ShowContextMenu(
                    message,
                    showMenu = showMenu,
                    actions = actions,
                    close = { showMenu = false },
                )
            }
        }
        if (isNewUserTask) {
            FilledIconButton(onClick = { actions.taskChatAction(message.taskId) }) {
                Icon(
                    imageVector = Icons.Default.Start,
                    contentDescription = ""
                )
            }
        }

    }
}

@Composable
fun MessageContent(
    message: MessageUiModel,
    actions: ChatActions,
    showMenu: () -> Unit,
) {
    when {
        message.isPdfMessage -> {
            message.messageData?.filePath?.let { filePath ->
                PDFMessage(
                    pdfUri = Uri.parse(filePath),
                    actions = actions,
                    showMenu = showMenu,
                    message = message
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
            Surface(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomEnd = 5.dp,
                    bottomStart = 14.dp
                ),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column {
                    MessageText(
                        showMenu = showMenu,
                        message = message,
                        modifier = Modifier
                            .padding(
                                start = 14.dp,
                                end = 15.dp,
                                top = 8.dp,
                                bottom = 3.dp
                            )
                    )
                    OpenGraphContent(message)
                    Row(Modifier.align(Alignment.End)) {
                        MessageTime(
                            formattedTimeOnly = message.formattedTimeOnly,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OpenGraphContent(message: MessageUiModel) {
    message.openGraphResult?.let { openGraphResult ->
        if (openGraphResult.title != null && openGraphResult.description != null) {
            OpenGraphView(openGraphResult)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageText(
    message: MessageUiModel,
    showMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (content, url) = handleUrlText(message.content)
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    Text(
        modifier = modifier
            .run {
                url?.let {
                    combinedClickable(
                        onClick = {
                            scope.launch { uriHandler.openUri(it) }
                        },
                        onLongClick = showMenu
                    )
                } ?: this
            },
        text = content,
        style = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    )
}

@Composable
private fun handleUrlText(text: String): Pair<AnnotatedString, String?> {
    val content = buildAnnotatedString {
        val urlColor = MaterialTheme.colorScheme.onPrimary
        appendUrlsWithStyle(text, urlPattern, urlColor)
    }

    val url = content.getStringAnnotations("URL", start = 0, end = content.length)
        .firstOrNull()?.item

    return Pair(content, url)
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


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
fun UserChatItemPreview() {
    MoreStuffTheme {
        UserChatItem(messageUiModel, ChatActions())
    }
}