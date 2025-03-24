package io.middlepoint.morestuff.shared.ui.screen.chat.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.middlepoint.morestuff.shared.domain.model.OpenGraphResult
import io.middlepoint.morestuff.shared.ui.utils.HttpClientProvider

private val logger = co.touchlab.kermit.Logger.withTag("OpenGraphView")

@Composable
fun OpenGraphView(openGraphResult: OpenGraphResult) {
  logger.d { "Rendering OpenGraphView with result: $openGraphResult" }

  Box(
    modifier = Modifier
      .scale(0.9f)
      .clip(RoundedCornerShape(10.dp))
      .background(
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
      ).padding(7.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      openGraphResult.title?.let { title ->
        logger.d { "Rendering title: $title" }
        Text(
          text = title,
          style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onPrimary
          )
        )
      } ?: logger.w { "Title is null" }

      openGraphResult.description?.let { description ->
        logger.d { "Rendering description: $description" }
        Text(
          text = description,
          fontSize = 9.sp,
          color = MaterialTheme.colorScheme.onPrimary
        )
      } ?: logger.w { "Description is null" }

      if (!openGraphResult.title.isNullOrBlank() || !openGraphResult.description.isNullOrBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          if (!openGraphResult.image.isNullOrBlank()) {
            ImageWithStateHandling(openGraphResult)
          } else {
            FallbackContent(openGraphResult.siteName)
          }
        }
      }
    }
  }
}

@Composable
private fun ImageWithStateHandling(openGraphResult: OpenGraphResult) {
  val context = LocalPlatformContext.current
  val imageLoader = remember(context) {
    CoilImageLoaderProvider.getInstance(context)
  }

  val imageUrl = openGraphResult.image ?: ""
  logger.d { "Attempting to load image: $imageUrl" }

  val request = ImageRequest.Builder(context)
    .data(imageUrl)
    .crossfade(true)
    .build()

  val painter = rememberAsyncImagePainter(
    model = request,
    imageLoader = imageLoader
  )

  val painterState by painter.state.collectAsState()

  var isLoading by remember { mutableStateOf(true) }
  var hasError by remember { mutableStateOf(false) }
  var isSuccess by remember { mutableStateOf(false) }

  when (painterState) {
    is AsyncImagePainter.State.Loading -> {
      isLoading = true
      hasError = false
      isSuccess = false
      logger.d { "⏳ Image is loading..." }
    }

    is AsyncImagePainter.State.Success -> {
      isLoading = false
      hasError = false
      isSuccess = true
      logger.d { "✅ Image loaded successfully" }
    }

    is AsyncImagePainter.State.Error -> {
      val error = (painterState as AsyncImagePainter.State.Error).result.throwable
      isLoading = false
      hasError = true
      isSuccess = false
      logger.e { "❌ Image loading error: ${error.message}" }
    }

    else -> {
      logger.d { "Other state: $painterState" }
    }
  }

  when {
    isSuccess -> {
      Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }

    hasError -> {
      FallbackContent(openGraphResult.siteName)
    }

    else -> {
      CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        strokeWidth = 2.dp
      )
    }
  }
}

@Composable
private fun FallbackContent(siteName: String?) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(8.dp)
    ) {
      Text(
        text = siteName ?: "Error loading image",
        style = TextStyle(
          fontWeight = FontWeight.Medium,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        textAlign = TextAlign.Center
      )
    }
  }
}


object CoilImageLoaderProvider {

  private var instance: ImageLoader? = null
  private val httpClientProvider = HttpClientProvider()

  private fun initialize(context: PlatformContext) {
    val client = httpClientProvider.createHttpClient(followRedirects = true)

    if (instance == null) {

      instance = ImageLoader.Builder(context)
        .components {
          try {
            add(KtorNetworkFetcherFactory(client))
          } catch (e: Exception) {
            co.touchlab.kermit.Logger.withTag("CoilInit")
              .e(e) { "Failed to add KtorNetworkFetcherFactory" }
          }
        }
        .build()
    }
  }


  fun getInstance(context: PlatformContext): ImageLoader {
    if (instance == null) {
      initialize(context)
    }
    return instance ?: ImageLoader(context)
  }
}