package co.softov.morestuff.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffTopBar(
    openDrawer: () -> Unit,
    showReview: () -> Unit,
) {
    Surface(shadowElevation = 5.dp) {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) },
            navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(Icons.Filled.Menu, "")
                }
            },
            actions = {
                IconButton(onClick = showReview) {
                    Icon(
                        painter = painterResource(id = R.drawable.priority_48px),
                        contentDescription = stringResource(R.string.cd_priority_review),
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun MoreStuffTopBarPreview() {
    MoreStuffTheme(darkTheme = true) {
        MoreStuffTopBar(
            openDrawer = {},
            showReview = {},
        )
    }
}