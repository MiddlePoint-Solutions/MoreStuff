package co.softov.morestuff.android.ui.pdf

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.components.NavigateBackIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewScreen(
    imagePath: String,
    onBack: () -> Unit,
    onSendPdf: (Uri) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {


        TopAppBar(
            title = {},
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                NavigateBackIconButton(onBack = onBack)
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            actions = {
                IconButton(onClick = { onSendPdf(imagePath.toUri()) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.cd_share),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
        )
        PdfViewer(
            modifier = Modifier.fillMaxSize(),
            uri = imagePath.toUri(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )

    }
}
