package co.softov.morestuff.android.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import co.softov.morestuff.android.ui.components.NavigateBackIconButton
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// TODO: we should pass in MessageId and create a ViewModel for this screen.
fun ImagePreviewScreen(
    imagePath: String,
    title: String,
    onBack: () -> Unit,
    onSendImage: (String) -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        if (scale * zoomChange >= 1f) {
            scale *= zoomChange
            offset += offsetChange
        }
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    NavigateBackIconButton(onBack = onBack)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { onSendImage(imagePath) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
            )
        },
        containerColor = Color.Transparent
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = imagePath)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build(),
                    imageLoader = LocalContext.current.imageLoader
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
            Row(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 23.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(4.dp)
                        .padding(start = 15.dp, end = 15.dp),
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}