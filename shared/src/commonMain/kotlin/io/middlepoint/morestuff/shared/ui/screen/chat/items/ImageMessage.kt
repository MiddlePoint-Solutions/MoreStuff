package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageMessage(
    message: MessageUiModel,
    actions: ChatActions,
    showMenu: () -> Unit,
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
                derivedStateOf { message.messageData?.filePath }
            }

            // TODO: Coil multiplatform
//            Image(
//                painter = rememberAsyncImagePainter(
//                    model = ImageRequest.Builder(LocalContext.current)
//                        .data(data = uri)
//                        .crossfade(true)
//                        .scale(Scale.FIT)
//                        .memoryCacheKey(message.messageData?.filePath)
//                        .build(),
//                    imageLoader = LocalContext.current.imageLoader
//                ),
//                contentDescription = null,
//                modifier = Modifier
//                    .padding(start = 1.dp, top = 1.dp, end = 1.dp, bottom = 10.dp)
//                    .combinedClickable(
//                        onClick = { actions.onImageSelected(message) },
//                        onLongClick = showMenu
//                    )
//                    .sizeIn(minHeight = 200.dp, maxHeight = 400.dp)
//                    .clip(RoundedCornerShape(8.dp)),
//                contentScale = ContentScale.Inside
//            )
            if (message.content.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(
                            start = 14.dp,
                            end = 15.dp,
                            bottom = 3.dp
                        ),
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(Modifier.align(Alignment.End)) {
                MessageTime(
                    formattedTimeOnly = message.formattedTimeOnly,
                    modifier = Modifier
                )
            }
        }
    }
}