package io.middlepoint.morestuff.shared.ui.screen.chat.task

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.arkivanov.essenty.backhandler.BackCallback
import io.github.xxfast.decompose.router.LocalRouterContext
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.components.keyboardAsState
import io.middlepoint.morestuff.shared.ui.extension.checkRegister
import io.middlepoint.morestuff.shared.ui.extension.checkUnregister
import io.middlepoint.morestuff.shared.ui.screen.settings.koinInjectOnRoute
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.cd_schedule_icon
import morestuff.composeapp.generated.resources.ic_schedule
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.core.parameter.parametersOf


@Composable
fun TaskDetails(
  taskId: Uuid,
  modifier: Modifier = Modifier,
  taskOptions: @Composable ColumnScope.() -> Unit = {},
  onTitleLineCount: (Int) -> Unit
) {

  val viewModel = koinInjectOnRoute(
    type = TaskDetailsViewModel::class,
    key = "TaskChat$taskId",
    parameters = { parametersOf(taskId) }
  )

  val model by viewModel.models.collectAsState()
  val task = model.task
  val isKeyboardOpen by keyboardAsState()
  val focusManager = LocalFocusManager.current
  var isEditing by remember { mutableStateOf(false) }
  var showSchedule by remember { mutableStateOf(false) }

  LaunchedEffect(isKeyboardOpen) {
    if (!isKeyboardOpen) {
      focusManager.clearFocus()
      isEditing = false
    }
  }

  val backCallback = remember {
    BackCallback {
      focusManager.clearFocus()
      isEditing = false
    }
  }

  val backHandler = LocalRouterContext.current.backHandler
  LaunchedEffect(isEditing) {
    if (isEditing) {
      backHandler.checkRegister(backCallback)
    } else {
      backHandler.checkUnregister(backCallback)
    }
  }

  Box(modifier = modifier) {
    Surface(
      modifier = Modifier
        .padding(bottom = 18.dp)
        .animateContentSize(),
      color = MaterialTheme.colorScheme.primaryContainer
    ) {
      Box(
        modifier = Modifier
          .then(
            if (isEditing) {
              Modifier.fillMaxHeight(0.7f)
            } else {
              Modifier.wrapContentHeight()
            }
          ),
      ) {

        val toggleModifier by rememberUpdatedState(
          Modifier.then(
            if (isEditing) {
              Modifier.alpha(0.5f)
            } else {
              Modifier
                .alpha(1f)
                .clip(CircleShape)
                .clickable { viewModel.take(TaskDetailsEvent.ToggleTaskComplete) }
            }
          )
        )

        Column(
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Row(
            modifier = Modifier.padding(bottom = 8.dp),
          ) {
            Box(
              Modifier
                .padding(start = 12.dp, end = 12.dp)
                .size(32.dp)
            ) {
              AnimatedContent(
                targetState = task.isComplete,
                label = "Complete toggle animation",
                transitionSpec = { scaleIn() togetherWith fadeOut() },
                modifier = Modifier.align(Alignment.Center)
              ) {
                when (it) {
                  true -> {
                    Icon(
                      imageVector = Icons.Filled.CheckCircle,
                      contentDescription = stringResource(Res.string.cd_schedule_icon),
                      modifier = Modifier
                        .fillMaxSize()
                        .then(toggleModifier),
                      tint = MaterialTheme.colorScheme.onSurface
                    )
                  }

                  false -> {
                    Icon(
                      imageVector = Icons.Outlined.Circle,
                      contentDescription = stringResource(Res.string.cd_schedule_icon),
                      modifier = Modifier
                        .fillMaxSize()
                        .then(toggleModifier),
                      tint = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }
            }

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(end = 39.dp)
            ) {
              var firstTime by remember { mutableStateOf(true) }
              var showEllipsis by remember { mutableStateOf(false) }
              var lineEnd by remember(model.taskTitle) { mutableIntStateOf(0) }

              var editingTitle by remember(model.taskTitle) {
                mutableStateOf(model.taskTitle)
              }

              val displayTitle by remember(editingTitle) {
                derivedStateOf {
                  if (showEllipsis) {
                    editingTitle.substring(0, lineEnd - 3) + "..."
                  } else {
                    editingTitle
                  }
                }
              }

              BasicTextField(
                value = if (isEditing || showSchedule) editingTitle else displayTitle,
                onValueChange = { newTitle ->
                  editingTitle = newTitle
                  viewModel.take(TaskDetailsEvent.UpdateTaskTitle(newTitle))
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .onFocusChanged {
                    isEditing = it.isFocused
                    if (it.isFocused) {
                      showSchedule = false
                    }
                  },
                enabled = !task.isComplete,
                keyboardOptions = KeyboardOptions(
                  capitalization = KeyboardCapitalization.Sentences,
                  autoCorrectEnabled = false,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions {
                  focusManager.clearFocus()
                },
                onTextLayout = {
                  if (isEditing || (firstTime && model.taskTitle.isNotEmpty())) {
                    firstTime = false
                    showEllipsis = it.lineCount > 2
                    lineEnd = if (showEllipsis) {
                      it.getLineEnd(1, visibleEnd = true)
                    } else {
                      it.getLineEnd(0, visibleEnd = true)
                    }
                  }
                  onTitleLineCount(it.lineCount)
                },
                maxLines = if (isEditing || showSchedule) 4 else 2,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                  color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
              )
            }
          }

          /*AnimatedVisibility(
            visible = !task.isComplete && !isEditing,
            modifier = Modifier.padding(horizontal = 16.dp),
          ) {
            Column(content = taskOptions)
          }*/

          Box(
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              AnimatedVisibility(visible = showSchedule) {
                TaskSchedule(
                  model = model.scheduleModel,
                  onTimeChange = { hour, minute ->
                    viewModel.take(
                      TaskDetailsEvent.UpdatePlanTime(
                        hour,
                        minute
                      )
                    )
                  },
                  onDateChange = { date ->
                    viewModel.take(TaskDetailsEvent.UpdatePlanDate(date))
                  },
                  createSchedule = { viewModel.take(TaskDetailsEvent.CreateOneTimeSchedule) },
                  cancelSchedule = { viewModel.take(TaskDetailsEvent.CancelActiveSchedule) },
                )
              }
            }
          }
        }
      }
    }

    AnimatedVisibility(
      visible = !isEditing && !task.isComplete,
      modifier = Modifier
        .padding(end = 6.dp)
        .align(Alignment.BottomEnd),
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Row(
        modifier = Modifier.background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
      ) {

        if (task.hasSchedule && !showSchedule) {
          Surface(
            modifier = Modifier.size(width = 35.dp, height = 35.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = CircleShape,
          ) {
            IconButton(onClick = { showSchedule = !showSchedule }) {
              Icon(
                imageVector = vectorResource(Res.drawable.ic_schedule),
                contentDescription = stringResource(Res.string.cd_schedule_icon),
              )
            }
          }
        }

        FilledIconButton(
          onClick = { showSchedule = !showSchedule },
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
          )
        ) {
          Icon(
            imageVector = if (showSchedule) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (showSchedule) "Hide Schedule" else "Show Schedule"
          )
        }
      }
    }
  }
}


//@Preview(
//  uiMode = Configuration.UI_MODE_NIGHT_YES,
//  name = "Dark"
//)
//@Preview(
//  uiMode = Configuration.UI_MODE_NIGHT_NO,
//  name = "Light"
//)
//@Composable
//fun TaskChatTopBarPreview() {
//  MoreStuffTheme {
//    TaskDetails(taskId = 1)
//  }
//}
