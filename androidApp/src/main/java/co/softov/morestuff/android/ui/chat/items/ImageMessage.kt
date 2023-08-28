package co.softov.morestuff.android.ui.chat.items

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.MessageData
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest

@Composable
fun ImageMessage(
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
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}