package co.softov.morestuff.android.ui.chat.items

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.chat.TaskActions
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.userChatItem
import kotlinx.coroutines.launch


/*@Composable
fun UserChatItem(message: Message, actions: TaskActions) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp)
            .clickable { actions.taskChatAction(message.taskId) },
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.padding(end = 10.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colorScheme.userChatItem,
                contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp), text = message.content
                )
            }
        }
    }
}*/

@Composable
fun UserChatItem(message: Message, actions: TaskActions) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp)
            .clickable { actions.taskChatAction(message.taskId) },
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.padding(end = 10.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colorScheme.userChatItem,
                contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
            ) {
                if (message.content.startsWith("http")) {
                    ClickableLink(text = message.content, uri = message.content)
                } else {
                    Text(
                        modifier = Modifier.padding(8.dp), text = message.content
                    )
                }
            }
        }
    }
}
@Composable
fun ClickableLink(text: String, uri: String) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
            append(text)
            addStringAnnotation(
                tag = "URL",
                annotation = uri,
                start = 0,
                end = text.length
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    ClickableText(
        modifier = Modifier.padding(8.dp),
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    coroutineScope.launch {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                        context.startActivity(intent)
                    }
                }
        }
    )
}



@Preview
@Composable
fun UserChatItemPreview() {
    MoreStuffTheme(darkTheme = true) {
        UserChatItem(message = MockData.Message.userNewTask, TaskActions())
    }
}