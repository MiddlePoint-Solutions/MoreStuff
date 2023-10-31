package co.softov.morestuff.android.ui.components

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffTopBar(
    reviewSelected: () -> Unit,
    settingsSelected: () -> Unit,
    searchSelected: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
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
                    painter = painterResource(id = R.drawable.ic_review_new),
                    contentDescription = stringResource(R.string.cd_priority_review),
                )
            }

            IconButton(onClick = settingsSelected) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.cd_priority_review),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
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