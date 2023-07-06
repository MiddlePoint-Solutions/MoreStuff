package co.softov.morestuff.android.ui.chat.items

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import co.softov.morestuff.android.domain.model.DataForMessage
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.chat.ChatActions
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.userChatItem
import co.softov.morestuff.android.ui.utils.appendUrlsWithStyle
import co.softov.morestuff.android.ui.utils.urlPattern
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import timber.log.Timber

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
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colorScheme.userChatItem,
                contentColor = contentColorFor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .padding(end = 5.dp, bottom = 4.dp)
                    .clickable {
                        showMenu = true
                    }

            ) {
                Column(
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .padding(end = 15.dp)
                            .run {
                                urls?.let {
                                    clickable {
                                        coroutineScope.launch {
                                            uriHandler.openUri(it)
                                        }
                                    }
                                } ?: this
                            },
                        text = content,
                        style = LocalTextStyle.current.copy(
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    )


                    if (openGraphResult?.title != null && openGraphResult.description != null) {
                        OpenGraphPreview(openGraphResult)
                    }

                    ShowContextMenu(
                        message,
                        onCopyMessage = actions.copyMessage,
                        onDeleteMessage = actions.deleteMessage,
                        showMenu = showMenu,
                        onClose = {
                            showMenu = false
                        },
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    message.imagePath?.let { imagePath ->
                        ImageOpenGraphPreview(imagePath)
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

@Composable
fun ImageOpenGraphPreview(dataForMessage: DataForMessage) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .scale(0.9f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(dataForMessage.filePath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}



@Preview
@Composable
fun UserChatItemPreview() {
    MoreStuffTheme() {
        UserChatItem(message = MockData.Message.userNewTask, ChatActions())
    }
}