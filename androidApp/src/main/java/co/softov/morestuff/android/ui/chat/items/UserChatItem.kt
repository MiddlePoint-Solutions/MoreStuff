package co.softov.morestuff.android.ui.chat.items

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.model.MessageData
import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.domain.util.TimeFormatter
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.userChatItem
import co.softov.morestuff.android.ui.utils.appendUrlsWithStyle
import co.softov.morestuff.android.ui.utils.urlPattern
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserChatItem(
    message: Message,
    actions: ChatActions,
) {
    var showMenu by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val urlPattern = urlPattern
    val openGraphResult = message.openGraphResult
    val timeFormatter: TimeFormatter = koinInject()

    val content by remember {
        derivedStateOf {
            buildAnnotatedString {
                appendUrlsWithStyle(message.content, urlPattern)
            }
        }
    }

    val urls by remember {
        derivedStateOf {
            try {
                content.getStringAnnotations("URL", start = 0, end = content.length).first().item
            } catch (e: Exception) {
                null
            }
        }
    }

    val haptic = LocalHapticFeedback.current
    LaunchedEffect(key1 = showMenu, block = {
        if (showMenu) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    })

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
            Box(
                modifier = Modifier
                    .padding(end = 5.dp, bottom = 4.dp)
                    .clickable {
                        showMenu = true
                    }
            ) {

                if (message.messageData?.filePath != null) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomEnd = 7.dp,
                            bottomStart = 10.dp
                        ),
                        color = MaterialTheme.colorScheme.userChatItem,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.primary),
                        modifier = if (message.content.isEmpty()) {
                            Modifier
                                .padding(2.dp)
                                .wrapContentWidth()
                                .wrapContentHeight()
                        } else {
                            Modifier
                                .padding(2.dp)
                                .fillMaxWidth()
                        }
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End
                        ) {
                            if (message.content.isNotEmpty()) {
                                Text(
                                    text = message.content,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .padding(end = 15.dp)
                                        .align(Alignment.Start),
                                    style = LocalTextStyle.current.copy(
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .aspectRatio(1f)
                                    .size(200.dp)
                            ) {
                                ChatImageMessage(
                                    messageData = message.messageData,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .combinedClickable(
                                            onClick = {
                                                actions.onImageSelected(message)
                                            },
                                            onLongClick = { showMenu = true }
                                        )
                                        .aspectRatio(1f)
                                        .size(200.dp),
                                )
                                timeFormatter.formatTimeOnly(message.createTime)?.let {
                                    Text(
                                        text = it,
                                        style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 12.dp, start = 30.dp, bottom = 5.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                MaterialTheme.colorScheme.background.copy(
                                                    alpha = 0.5F
                                                )
                                            )
                                            .padding(4.dp)
                                    )
                                }

                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomEnd = 5.dp,
                            bottomStart = 14.dp
                        ),
                        color = MaterialTheme.colorScheme.userChatItem,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.primary),
                    ) {
                        Column(

                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 15.dp, bottom = 5.dp)
                                    .run {
                                        urls?.let {
                                            combinedClickable(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        uriHandler.openUri(it)
                                                    }
                                                },
                                                onLongClick = { showMenu = true }
                                            )
                                        } ?: this
                                    },
                                text = content,
                                style = LocalTextStyle.current.copy(
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            )
                            timeFormatter.formatTimeOnly(message.createTime)?.let {
                                Text(
                                    text = it,
                                    style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                                    modifier = Modifier.align(Alignment.End)
                                        .padding(end = 12.dp, start = 30.dp, bottom = 5.dp)
                                )
                            }
                            if (openGraphResult?.title != null && openGraphResult.description != null) {
                                OpenGraphPreview(openGraphResult)
                            }
                        }
                    }
                }

                ShowContextMenu(
                    message,
                    onCopyMessage = actions.copyMessage,
                    onDeleteMessage = actions.deleteMessage,
                    showMenu = showMenu,
                    onClose = {
                        showMenu = false
                    },
                    modifier = Modifier.padding(top = 20.dp),
                    onShareImage = actions.shareImage
                )
            }
        }
    }
}

@Composable
private fun ChatImageMessage(
    messageData: MessageData,
    modifier: Modifier = Modifier,
) {
    val uri: Uri = Uri.parse(messageData.filePath)

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = uri)
                .apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                }).build(), imageLoader = LocalContext.current.imageLoader
        ),
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1f)
            .size(200.dp)
            .border(2.dp, MaterialTheme.colorScheme.userChatItem, RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}


@Composable
private fun OpenGraphPreview(openGraphResult: OpenGraphResult) {
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
    MoreStuffTheme() {
        UserChatItem(message = MockData.Message.userNewTask, ChatActions())
    }
}