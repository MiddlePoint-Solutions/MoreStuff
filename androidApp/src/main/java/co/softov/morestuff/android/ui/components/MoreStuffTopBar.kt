package co.softov.morestuff.android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.schedule.PriorityViewModel
import co.softov.morestuff.android.ui.theme.surfaceContainer
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreStuffTopBar(
    reviewSelected: () -> Unit,
    settingsSelected: () -> Unit,
    searchSelected: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    itemSelectedState: MutableState<Boolean>,

    ) {
    val viewModel: PriorityViewModel = koinViewModel()
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            if (itemSelectedState.value) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.cd_priority_review),
                    )
                }
                IconButton(onClick = {
                    viewModel.completeSelectedTask()
                    itemSelectedState.value = false
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = stringResource(R.string.cd_priority_review),
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Rounded.Numbers,
                        contentDescription = stringResource(R.string.cd_priority_review),
                    )
                }
            } else {
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
            }


        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        scrollBehavior = scrollBehavior
    )
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun MoreStuffTopBarPreview() {
    MoreStuffTheme() {
        MoreStuffTopBar(
            reviewSelected = {},
            settingsSelected = {},
            searchSelected = { },
            itemSelectedState =
        )
    }
}*/
