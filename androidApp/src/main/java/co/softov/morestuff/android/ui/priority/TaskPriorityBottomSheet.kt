package co.softov.morestuff.android.ui.priority

import androidx.compose.animation.AnimatedVisibility
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
import app.cash.molecule.RecompositionClock
import app.cash.molecule.launchMolecule
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.presentation.model.PriorityModel
import co.softov.morestuff.android.presentation.model.mapToModel
import co.softov.morestuff.android.presentation.presenter.PriorityOptionsPresenter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPriorityBottomSheet(
    taskId: Long,
    openBottomSheet: Boolean,
    bottomSheetState: SheetState = rememberSheetState(skipHalfExpanded = false),
    dismissAction: () -> Unit,
) {

    val scope = rememberCoroutineScope()

    val viewModel = getViewModel<TaskPriorityViewModel> {
        parametersOf(taskId)
    }

//    var priority: PriorityModel by remember { mutableStateOf(PriorityModel.Today(DefaultOption.Auto)) }
//
//    val priorityOptions by scope.launchMolecule(clock = RecompositionClock.ContextClock) {
//        PriorityOptionsPresenter()
//    }.collectAsState()

    val priorityOptions by viewModel.priorityOptions.collectAsState()

    var showConfirm by remember { mutableStateOf(true) }

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
                visible = showConfirm,
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
                        onSelected = {
                            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    dismissAction()
                                }
                            }
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(),
                        text = "Confirm".uppercase(),
                        fontSize = 20.sp
                    )
                }
            }

            PriorityInput(
                priority = priorityOptions.current.mapToModel(),
                onPriorityChange = {
                    viewModel.priorityChanged(it)
                    showConfirm = true
                },
                priorityOptions = priorityOptions,
                onPriorityOptionChange = { showConfirm = true }
            )
        }
    }
}