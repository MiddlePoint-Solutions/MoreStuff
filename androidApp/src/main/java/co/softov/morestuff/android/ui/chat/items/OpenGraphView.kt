package co.softov.morestuff.android.ui.chat.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.OpenGraphResult
import co.softov.morestuff.android.ui.theme.isDarkTheme
import coil.compose.rememberAsyncImagePainter


@Composable
fun OpenGraphView(openGraphResult: OpenGraphResult) {
    Box(
        modifier = Modifier
            .scale(0.9f)
            .clip(RoundedCornerShape(10.dp))
            .background(BackgroundOpenGraphColor())
            .padding(7.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = openGraphResult.title ?: "",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Text(
                text = openGraphResult.description ?: "",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )

            if (openGraphResult.image != null) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(openGraphResult.image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
@Composable
fun BackgroundOpenGraphColor(): Color {
    val lightPrimary = Color(0xFF6A7BD4)
    val darkPrimary = Color(0x66FFFFFF)
    val isDarkTheme = isDarkTheme()
    return if (isDarkTheme) darkPrimary else lightPrimary
}

