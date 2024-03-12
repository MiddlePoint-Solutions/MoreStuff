package co.softov.morestuff.android.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.model.Digit
import co.softov.morestuff.android.ui.model.compareTo
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.surfaceContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    containerColor: Color,
    selectedTaskCount: Int,
    reviewSelected: () -> Unit,
    settingsSelected: () -> Unit,
    searchAction: () -> Unit,
    clearTaskSelection: () -> Unit,
    completeSelectedTasks: () -> Unit,
    deleteSelectedTasks: () -> Unit,
    selectScope: () -> Unit,
) {

    val taskSelectionActive = remember(selectedTaskCount) { selectedTaskCount > 0 }
    val transition = updateTransition(taskSelectionActive, label = "Selection state")
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            transition.AnimatedContent { targetState ->
                if (targetState) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = clearTaskSelection) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Row(
                                modifier = Modifier.animateContentSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AnimatedCounter(count = selectedTaskCount)
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        actions = {
            if (taskSelectionActive) {
                IconButton(onClick = completeSelectedTasks) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = stringResource(R.string.cd_complete_tasks)
                    )
                }

                IconButton(onClick = deleteSelectedTasks) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.cd_delete_tasks)
                    )
                }

                IconButton(onClick = selectScope) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scope_add),
                        contentDescription = stringResource(R.string.choose_scope)
                    )
                }
            } else {
                IconButton(onClick = searchAction) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.cd_search_tasks)
                    )
                }

                IconButton(onClick = reviewSelected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_review_new),
                        contentDescription = stringResource(R.string.cd_priority_review)
                    )
                }

                IconButton(onClick = settingsSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.cd_open_settings)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
    )
}

@Composable
private fun AnimatedCounter(count: Int) {
    Row(
        modifier = Modifier.animateContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        count.toString()
            .mapIndexed { index, c -> Digit(c, count, index) }
            .forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } togetherWith slideOutVertically { it }
                        } else {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        }
                    }, label = ""
                ) {
                    Text(
                        text = "${it.digitChar}",
                        textAlign = TextAlign.Center,
                    )
                }
            }
    }
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
private fun Preview() {
    MoreStuffTheme {
        HomeTopBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            selectedTaskCount = 0,
            reviewSelected = {},
            settingsSelected = {},
            searchAction = { },
            clearTaskSelection = {},
            completeSelectedTasks = {},
            deleteSelectedTasks = {},
            selectScope = {},
        )
    }
}
