package co.softov.morestuff.android.ui.chat.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.domain.usecase.message.findFirstUrl
import co.softov.morestuff.android.ui.chat.TaskActions
import co.softov.morestuff.android.ui.chat.task.TaskChatViewModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.userChatItem
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.regex.Pattern

@Composable
fun UserChatItem(message: Message, actions: TaskActions) {
    val taskId = message.taskId
    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM") {
        parametersOf(taskId)
    }
    val messageId = message.id
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val urlPattern = Pattern.compile(
        "(https?://|www\\.|[a-zA-Z0-9_-]+\\.)?[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?",
        Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
    )

    val firstUrl = findFirstUrl(message.content, urlPattern)
    val openGraphResult = getOpenGraphData(firstUrl, messageId)

    val text = buildAnnotatedString {
        appendUrlsWithStyle(message.content, urlPattern)
    }

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
                Column(modifier = Modifier.padding(end = 5.dp, bottom = 4.dp)) {

                    SelectionContainer {
                        ClickableText(
                            modifier = Modifier.padding(8.dp),
                            text = text,
                            style = LocalTextStyle.current.copy(
                                color = Color.White,
                                fontSize = 16.sp
                            ),
                            onClick = { offset ->
                                text.getStringAnnotations("URL", offset, offset).firstOrNull()
                                    ?.let {
                                        coroutineScope.launch {
                                            try {
                                                uriHandler.openUri(it.item)
                                            } catch (e: Exception) {
                                            }
                                        }
                                    }
                            }
                        )
                    }
                    if (openGraphResult?.title != null && openGraphResult.description != null) {
                        OpenGraphPreview(openGraphResult)
                    }

                }
            }
        }
    }
}


@Composable
fun getOpenGraphData(firstUrl: String?, messageId: Long): OpenGraphResult? {
    val viewModel = getViewModel<TaskChatViewModel>(key = "TaskChatVM")
    return produceState<OpenGraphResult?>(initialValue = null) {
        value = if (firstUrl != null) {
            viewModel.fetchOpenGraphMetadata(firstUrl, messageId)
        } else null
    }.value
}

fun AnnotatedString.Builder.appendUrlsWithStyle(content: String, urlPattern: Pattern) {
    val matcher = urlPattern.matcher(content)
    var lastEnd = 0

    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()

        if (start > lastEnd) {
            append(content.substring(lastEnd, start))
        }
        val originalUrl = content.substring(start, end)
        var processedUrl = originalUrl
        if (!processedUrl.startsWith("http://") && !processedUrl.startsWith("https://")) {
            processedUrl = "https://$processedUrl"
        }
        pushStringAnnotation("URL", processedUrl)
        withStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = Color.White,
                fontSize = 16.sp
            )
        ) {
            append(originalUrl)
        }
        pop()
        lastEnd = end
    }
    if (lastEnd < content.length) {
        append(content.substring(lastEnd))
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
        UserChatItem(message = MockData.Message.userNewTask, TaskActions())
    }
}