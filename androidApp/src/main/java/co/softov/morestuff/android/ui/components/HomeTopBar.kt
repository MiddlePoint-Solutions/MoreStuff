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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.home.HomeUiModel
import co.softov.morestuff.android.ui.model.Digit
import co.softov.morestuff.android.ui.model.compareTo
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    model: HomeUiModel,
    reviewSelected: () -> Unit,
    settingsSelected: () -> Unit,
    searchSelected: () -> Unit,
    clearTaskSelection: () -> Unit,
    completeSelectedTasks: () -> Unit,
    deleteSelectedTasks: () -> Unit,
    selectScope: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: State<Color>,
) {

    val taskSelectionActive = remember(model) { model.taskSelectionActive }
    val transition = updateTransition(taskSelectionActive, label = "Selection state")

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
                                AnimatedCounter(count = model.selectedTaskIds.size)
                            }
                        }
                    }
                } else {
                    Text(text = stringResource(id = R.string.app_name))
                }
            }
        },
        actions = {
            if (taskSelectionActive) {
                IconButton(onClick = deleteSelectedTasks) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.cd_priority_review)
                    )
                }
                IconButton(onClick = selectScope) {
                    Icon(
                        imageVector = Icons.Rounded.Layers,
                        contentDescription = stringResource(R.string.cd_priority_review)
                    )
                }

                IconButton(onClick = completeSelectedTasks) {
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = stringResource(R.string.cd_priority_review)
                    )
                }
            } else {
                IconButton(onClick = searchSelected) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.cd_priority_review)
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
                        contentDescription = stringResource(R.string.cd_priority_review)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor.value),
            scrollBehavior = scrollBehavior
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
        val previewContainerColor = remember { mutableStateOf(Color.Blue) }
        HomeTopBar(
            model = HomeUiModel(),
            reviewSelected = {},
            settingsSelected = {},
            searchSelected = { },
            clearTaskSelection = {},
            completeSelectedTasks = {},
            deleteSelectedTasks = {},
            selectScope = {},
            containerColor = previewContainerColor
        )
    }
}
