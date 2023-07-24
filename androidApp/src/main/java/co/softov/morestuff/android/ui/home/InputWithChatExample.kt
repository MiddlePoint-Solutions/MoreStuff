/*
package co.softov.morestuff.android.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.softov.morestuff.android.ui.chat.Messages
import co.softov.morestuff.android.ui.input.UserInput
import co.softov.morestuff.android.ui.input.UserTextInput
import co.softov.morestuff.android.ui.input.VoiceToTextInput
import co.softov.morestuff.android.ui.priority.PriorityInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

Box {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = true
        )
    )

    ConstraintLayout {
        val (bottomSheet, userInput) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(bottomSheet) { bottom.linkTo(userInput.top) }
                .fillMaxSize(),
        ) {
            BottomSheetScaffold(
                scaffoldState = bottomSheetScaffoldState,
                sheetContent = {
                    Messages(
                        messages = messages,
                        actions = chatActions,
                        modifier = modifier,
                        scrollState = chatScrollState
                    )
                },
                sheetPeekHeight = 0.dp
            ) {}
        }

        val priorityModel by userInputViewModel.priorityModel.collectAsStateWithLifecycle()

        UserInput(
            modifier = Modifier
                .constrainAs(userInput) { bottom.linkTo(parent.bottom) }
                .imePadding(),
            priorityContent = {
                PriorityInput(
                    model = priorityModel,
                    onNowSelected = userInputViewModel::setNowPriority,
                    onLaterSelected = userInputViewModel::setLaterPriority,
                    onPlanSelected = userInputViewModel::setPlanPriority,
                    onTimeChange = userInputViewModel::updatePlanTime,
                    onDateChange = userInputViewModel::updatePlanDate,
                )
            },
            textContent = {
                UserTextInput(
                    value = userInputValue,
                    onValueChange = { userInputValue = it },
                    sendAction = {
                        userInputViewModel.createNewTask(it)
                        userInputValue = userInputValue.copy("")
                        scope.launch {
                            delay(200)
                            priorityScrollState.animateScrollToItem(index = 0)
                        }
                    },
                    actionsContent = {
                        VoiceToTextInput(
                            onUpdateValue = userInputViewModel::updateUserInput
                        )
                    }
                )
            },
        )
    }
}*/
