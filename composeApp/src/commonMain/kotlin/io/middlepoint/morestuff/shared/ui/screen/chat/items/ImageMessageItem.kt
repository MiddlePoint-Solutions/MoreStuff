package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import io.middlepoint.morestuff.shared.ui.model.MessageUiModel

@Composable
fun ImageMessageItem(
  message: MessageUiModel,
) {
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.End,
    modifier = Modifier.clip(RoundedCornerShape(16.dp))
  ) {

    val uri by remember {
      derivedStateOf { message.messageExtra?.filePath }
    }
    val platformContext = LocalPlatformContext.current
    val imageLoader = remember(platformContext) {
      ImageLoader(platformContext).newBuilder()
    }.build()

    Image(
      painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalPlatformContext.current)
          .data(data = uri)
          .crossfade(true)
          .scale(Scale.FIT)
          .memoryCacheKey(message.messageExtra?.filePath)
          .build(),
        imageLoader = imageLoader
      ),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .padding(start = 1.dp, top = 1.dp, end = 1.dp, bottom = 10.dp)
        .sizeIn(maxHeight = 400.dp)
    )

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
        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
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


/*
fun createImageLoader(context: PlatformContext): ImageLoader {
  return ImageLoader.Builder(context)
    .components {
      add(getPlatformFetcherFactory())
      //add(KmpFileFetcher.Factory())
    }
    .crossfade(true)
    //.logger(DebugLogger()) // Útil para depuración, puedes eliminar en producción
    .build()
}*/
