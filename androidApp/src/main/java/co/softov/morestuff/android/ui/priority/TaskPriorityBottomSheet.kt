package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.presentation.model.PriorityOptionsModel
import co.softov.morestuff.android.presentation.model.mapToModel
import co.softov.morestuff.android.ui.chat.task.model.TaskPriorityModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPriorityBottomSheet(
    model: TaskPriorityModel,
    openBottomSheet: Boolean = false,
    bottomSheetState: SheetState = rememberSheetState(skipHalfExpanded = false),
    priorityChangeAction: (Priority) -> Unit = {},
    priorityOptionChangeAction: (PriorityOption) -> Unit = {},
    confirmationAction: () -> Unit = {},
    dismissAction: () -> Unit = {},
) {
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = dismissAction,
            sheetState = bottomSheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    shape = MaterialTheme.shapes.small
                )
            }
        ) {

            AnimatedVisibility(
                visible = model.showConfirmation,
                enter = expandVertically(
                    animationSpec = spring()
                ),
                exit = shrinkVertically(
                    animationSpec = spring()
                )
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false,
                ) {
                    PriorityButton(
                        onSelected = confirmationAction,
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(),
                        text = "Confirm".uppercase(),
                        fontSize = 20.sp
                    )
                }
            }

            PriorityInput(
                priority = model.priorityModel.current.mapToModel(),
                onPriorityChange = priorityChangeAction,
                priorityOptions = model.priorityModel,
                onPriorityOptionChange = priorityOptionChangeAction
            )
        }
    }
}