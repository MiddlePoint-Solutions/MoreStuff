package co.softov.morestuff.android.ui.chat.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.chat.TaskActions
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.userChatItem
import co.softov.morestuff.android.ui.utils.appendUrlsWithStyle
import co.softov.morestuff.android.ui.utils.urlPattern
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserChatItem(
    message: Message,
    actions: TaskActions,
    onCopyMessage: (Message) -> Unit,
    onDeleteMessage: (Message) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val urlPattern = urlPattern
    val openGraphResult = message.openGraphResult
    val haptic = LocalHapticFeedback.current

    val text = buildAnnotatedString {
        appendUrlsWithStyle(message.content, urlPattern)
    }

    val urls = text.getStringAnnotations("URL", start = 0, end = text.length)
    val hasUrl = urls.isNotEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .padding(end = 10.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colorScheme.userChatItem,
                contentColor = contentColorFor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(end = 5.dp, bottom = 4.dp)
                    .combinedClickable(
                        onClick = {
                            if (hasUrl) {
                                coroutineScope.launch {
                                    try {
                                        uriHandler.openUri(urls.first().item)
                                    } catch (e: Exception) {
                                    }
                                }
                            } else {
                                actions.taskChatAction(message.taskId)
                            }
                        },
                        onLongClick = {
                            showMenu = true
                            isSelected = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 4.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = text,
                        style = LocalTextStyle.current.copy(
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )

                    if (openGraphResult?.title != null && openGraphResult.description != null) {
                        OpenGraphPreview(openGraphResult)
                    }

                    if (showMenu) {
                        ShowContextMenu(
                            message,
                            onCopyMessage = onCopyMessage,
                            onDeleteMessage = onDeleteMessage,
                            showMenu = showMenu,
                            onClose = {
                                showMenu = false
                                isSelected = false
                            },
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun OpenGraphPreview(openGraphResult: OpenGraphResult) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .scale(0.9f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = openGraphResult.title ?: "",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                modifier = Modifier.weight(1f)
            )
            if (openGraphResult.image != null) {
                Box(modifier = Modifier.size(40.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(openGraphResult.image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Text(
            text = openGraphResult.description ?: "",
            fontSize = 9.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Preview
@Composable
fun UserChatItemPreview() {
    MoreStuffTheme(darkTheme = true) {
        UserChatItem(message = MockData.Message.userNewTask, TaskActions(), onCopyMessage = {},
            onDeleteMessage = {})
    }
}