package co.softov.morestuff.android.ui.chat.items

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.enums.ContentType
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.chat.items.MockData.messageUiModel
import co.softov.morestuff.android.ui.chat.task.MessageUiModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.utils.appendUrlsWithStyle
import co.softov.morestuff.android.ui.utils.urlPattern
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Scale
import kotlinx.coroutines.launch


@Composable
fun UserChatItem(
    messageUiModel: MessageUiModel,
    actions: ChatActions,
) {
    val showMenu = remember { mutableStateOf(false) }
    val isNewUserTask by remember {
        derivedStateOf { messageUiModel.message.contentType == ContentType.USER_NEW_TASK }
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
                    .clickable { showMenu.value = true }
            ) {
                MessageContent(
                    messageUiModel = messageUiModel,
                    actions = actions,
                    showMenu = showMenu,
                )

                ShowContextMenu(
                    messageUiModel.message,
                    showMenu = showMenu.value,
                    copyMessage = actions.copyMessage,
                    deleteMessage = actions.deleteMessage,
                    close = { showMenu.value = false },
                    shareImage = actions.shareImage,
                    shareMessage = actions.shareMessage
                )
            }
        }
        if (isNewUserTask) {
            FilledIconButton(onClick = { actions.taskChatAction(messageUiModel.message.taskId) }) {
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
    messageUiModel: MessageUiModel,
    actions: ChatActions,
    showMenu: MutableState<Boolean>,
) {
    if (messageUiModel.message.isDataMessage) {
        MessageImage(
            message = messageUiModel,
            actions = actions,
            showMenu = showMenu,
        )
    } else {
        val openGraphResult = messageUiModel.message.openGraphResult
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
                    messageUiModel = messageUiModel,
                    modifier = Modifier
                        .padding(
                            start = 14.dp,
                            end = 15.dp,
                            top = 8.dp,
                            bottom = 3.dp
                        )
                )
                if (openGraphResult?.title != null && openGraphResult.description != null) {
                    OpenGraphView(openGraphResult)
                }
                Row(Modifier.align(Alignment.End)) {
                    MessageTime(
                        formattedTimeOnly = messageUiModel.formattedTimeOnly,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageImage(
    message: MessageUiModel,
    actions: ChatActions,
    showMenu: MutableState<Boolean>,
) {
    Surface(
        shape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp,
            bottomEnd = 7.dp,
            bottomStart = 10.dp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            val uri by remember {
                derivedStateOf { Uri.parse(message.message.messageData?.filePath) }
            }

            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data = uri)
                        .crossfade(true)
                        .scale(Scale.FIT)
                        .memoryCacheKey(message.message.messageData?.filePath)
                        .build(),
                    imageLoader = LocalContext.current.imageLoader
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 1.dp, top = 1.dp, end = 1.dp, bottom = 10.dp)
                    .combinedClickable(
                        onClick = { actions.onImageSelected(message.message) },
                        onLongClick = { showMenu.value = true }
                    )
                    .sizeIn(minHeight = 200.dp, maxHeight = 400.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Inside
            )
            if (message.message.content.isNotEmpty()) {
                MessageText(
                    showMenu = showMenu,
                    messageUiModel = message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start)
                        .padding(
                            start = 14.dp,
                            end = 15.dp,
                        )
                )
            }
            Row(Modifier.align(Alignment.End)) {
                MessageTime(
                    formattedTimeOnly = messageUiModel.formattedTimeOnly,
                    modifier = Modifier
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageText(
    messageUiModel: MessageUiModel,
    showMenu: MutableState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val message = messageUiModel.message
    val urlColor = rememberUpdatedState(MaterialTheme.colorScheme.onPrimary)
    val content by remember {
        derivedStateOf {
            buildAnnotatedString {
                appendUrlsWithStyle(message.content, urlPattern, urlColor.value)
            }
        }
    }

    val urls by remember {
        derivedStateOf {
            content.getStringAnnotations("URL", start = 0, end = content.length)
                .firstOrNull()?.item
        }
    }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    Text(
        modifier = modifier
            .run {
                urls?.let {
                    combinedClickable(
                        onClick = {
                            scope.launch {
                                uriHandler.openUri(it)
                            }
                        },
                        onLongClick = { showMenu.value = true }
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
fun MessageTime(
    formattedTimeOnly: String?,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    formattedTimeOnly?.let {
        Text(
            text = it,
            style = TextStyle(
                fontSize = 10.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight(400),
                color = textColor
            ),
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