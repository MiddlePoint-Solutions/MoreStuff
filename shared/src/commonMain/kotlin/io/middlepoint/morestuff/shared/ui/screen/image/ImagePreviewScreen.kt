package io.middlepoint.morestuff.shared.ui.screen.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.components.NavigateBackIconButton
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.cd_share
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// TODO: we should pass in MessageId and create a ViewModel for this screen.
fun ImagePreviewScreen(
    imagePath: String,
    title: String,
    onBack: () -> Unit,
    onSendImage: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

//        ZoomableAsyncImage( // TODO: Coil multiplatform
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(imagePath)
//                .crossfade(true)
//                .build(),
//            imageLoader = LocalContext.current.imageLoader,
//            contentDescription = "Selected Image",
//            modifier = Modifier
//                .fillMaxSize()
//        )

        TopAppBar(
            title = { },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            navigationIcon = {
                NavigateBackIconButton(onBack = onBack)
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            actions = {
                IconButton(onClick = onSendImage) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(Res.string.cd_share),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(bottom = 23.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(start = 15.dp, end = 15.dp),
                style = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            )
        }
    }
}