package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.Message
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.userChatItem

@Composable
fun UserChatItem(message: Message) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 45.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier.padding(end = 10.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(corner = CornerSize(8.dp)),
                color = MaterialTheme.colors.userChatItem,
                contentColor = contentColorFor(MaterialTheme.colors.primary)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp), text = message.content
                )
            }
        }
    }
}

@Preview
@Composable
fun UserChatItemPreview() {
    MoreStuffTheme(darkTheme = true) {
        UserChatItem(message = MockData.Message.userNewTask)
    }
}