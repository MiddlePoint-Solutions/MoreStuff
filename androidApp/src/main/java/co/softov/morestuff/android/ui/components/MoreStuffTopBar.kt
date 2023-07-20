package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffTopBar(
    reviewSelected: () -> Unit,
    settingsSelected: () -> Unit,
    searchSelected: () -> Unit
) {
    Surface(shadowElevation = 5.dp) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            actions = {

                IconButton(onClick = searchSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.cd_priority_review),
                    )
                }

                IconButton(onClick = reviewSelected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_review_48px),
                        contentDescription = stringResource(R.string.cd_priority_review),
                    )
                }

                IconButton(onClick = settingsSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.cd_priority_review),
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun MoreStuffTopBarPreview() {
    MoreStuffTheme() {
        MoreStuffTopBar(
            reviewSelected = {},
            settingsSelected = {},
            searchSelected = { },
        )
    }
}