package co.softov.morestuff.android.ui.image

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.Message
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    imagePath: String,
    message: Message,
    cancel: () -> Unit,
    onSendImage: (String) -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        if (scale * zoomChange >= 1f) {
            scale *= zoomChange
            offset += offsetChange
        }
    }

    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)

    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = cancel) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Cancel",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onSendImage(imagePath) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                },
            )

            val imageUri = Uri.parse(imagePath)
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imageUri)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build(), imageLoader = LocalContext.current.imageLoader
                ),
                contentDescription = "Selected Image",

                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.8f)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = if (scale > 1.3f) offset.x else 0f,
                        translationY = if (scale > 1.3f) offset.y else 0f
                    )
                    .transformable(state = state)
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            scope.launch {
                                scale = 1f
                                offset = Offset.Zero
                            }
                        })
                    },
            )
            Text(
                text = message.content,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(4.dp)
                    .padding(start = 15.dp, end = 15.dp)
                    .align(Alignment.CenterHorizontally),
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 16.sp
                )
            )
        }
    }
}