package io.middlepoint.morestuff.shared.ui.components

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
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.middlepoint.morestuff.shared.ui.model.Digit
import io.middlepoint.morestuff.shared.ui.model.compareTo
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.app_name
import morestuff.composeapp.generated.resources.cd_complete_tasks
import morestuff.composeapp.generated.resources.cd_delete_tasks
import morestuff.composeapp.generated.resources.cd_open_settings
import morestuff.composeapp.generated.resources.cd_search_tasks
import morestuff.composeapp.generated.resources.choose_scope
import morestuff.composeapp.generated.resources.ic_scope_add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
  username: String,
  containerColor: Color,
  selectedTaskCount: Int,
  settingsSelected: () -> Unit,
  searchAction: () -> Unit,
  clearTaskSelection: () -> Unit,
  completeSelectedTasks: () -> Unit,
  deleteSelectedTasks: () -> Unit,
  selectScope: () -> Unit,
  isReorderingActive: Boolean,
  modifier: Modifier = Modifier
) {

  val taskSelectionActive = remember(selectedTaskCount, isReorderingActive) {
    selectedTaskCount > 0 || isReorderingActive
  }
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
                AnimatedCounter(count = selectedTaskCount)
              }
            }
          }
        } else {
          Text(
            text = stringResource(Res.string.app_name),
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    },
    modifier = modifier,
    actions = {
      if (taskSelectionActive) {
        taskSelectionItems(
          completeSelectedTasks = completeSelectedTasks,
          deleteSelectedTasks = deleteSelectedTasks,
          selectScope = selectScope
        )
      } else {
        IconButton(onClick = searchAction) {
          Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = stringResource(Res.string.cd_search_tasks)
          )
        }

        IconButton(onClick = settingsSelected) {
          TaskProfile(title = username)
        }

      }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
  )
}

@Composable
private fun taskSelectionItems(
  completeSelectedTasks: () -> Unit,
  deleteSelectedTasks: () -> Unit,
  selectScope: () -> Unit
) {
  IconButton(onClick = completeSelectedTasks) {
    Icon(
      imageVector = Icons.Rounded.Done,
      contentDescription = stringResource(Res.string.cd_complete_tasks)
    )
  }

  IconButton(onClick = deleteSelectedTasks) {
    Icon(
      imageVector = Icons.Rounded.Delete,
      contentDescription = stringResource(Res.string.cd_delete_tasks)
    )
  }

  IconButton(onClick = selectScope) {
    Icon(
      painter = painterResource(Res.drawable.ic_scope_add),
      contentDescription = stringResource(Res.string.choose_scope)
    )
  }
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        HomeTopBar(
//            containerColor = MaterialTheme.colorScheme.surfaceContainer,
//            selectedTaskCount = 0,
//            reviewSelected = {},
//            settingsSelected = {},
//            searchAction = { },
//            clearTaskSelection = {},
//            completeSelectedTasks = {},
//            deleteSelectedTasks = {},
//            selectScope = {},
//        )
//    }
//}
