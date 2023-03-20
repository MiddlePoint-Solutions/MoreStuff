package co.softov.morestuff.android.ui.main.input

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.model.DefaultOption
import co.softov.morestuff.android.domain.model.Priority.*
import co.softov.morestuff.android.domain.model.PriorityOption
import co.softov.morestuff.android.domain.model.PriorityOptionsModel
import co.softov.morestuff.android.domain.model.TimeOfDayOption
import co.softov.morestuff.android.ui.priority.PriorityButton
import co.softov.morestuff.android.ui.theme.MoreStuffTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriorityOptions(
    modifier: Modifier = Modifier,
    model: PriorityOptionsModel,
    onOptionSelected: (PriorityOption) -> Unit,
) {

    val options = model.options
    val current = model.priority

    val maxItemsInRow = 3
    val firstRow: List<PriorityOption>
    val secondRow: MutableList<PriorityOption> = mutableListOf()
    if (options.size > maxItemsInRow) {
        firstRow = options.slice(0 until maxItemsInRow)
        secondRow.addAll(options.slice(maxItemsInRow until options.size))
    } else {
        firstRow = options
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            firstRow.forEach {
                PriorityButton(
                    onSelected = { onOptionSelected(it) },
                    text = it.toString(),
                    shape = MaterialTheme.shapes.small,
                    selected = it == current.option
                )
            }
        }

        if (secondRow.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                secondRow.forEach {
                    PriorityButton(
                        onSelected = { onOptionSelected(it) },
                        text = it.toString(),
                        shape = MaterialTheme.shapes.small,
                        selected = it == current.option
                    )
                }
            }
        }

        val timePickerState = rememberTimePickerState()

        if (current.option == DefaultOption.Custom) {
            when (current) {
                is Today -> {
                    TimePicker(state = timePickerState)
                }
                is Tomorrow -> {
                    TimePicker(state = timePickerState)
                }
                is Later -> {
                    val datePickerState = rememberDatePickerState()
                    val confirmEnabled = remember {
                        derivedStateOf { datePickerState.selectedDateMillis != null }
                    }


                    var openDialog by remember { mutableStateOf(true) }
                    if (openDialog) {
                        DatePickerDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user clicks outside the dialog or on the back
                                // button. If you want to disable that functionality, simply use an empty
                                // onDismissRequest.
                                openDialog = false

                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        openDialog = false
                                    },
                                    enabled = confirmEnabled.value
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        openDialog = false
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        ) {
//                            DatePicker(state = datePickerState)
                            TimePicker(state = timePickerState)
                        }
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun PriorityOptionsPreviewDark() {
    val options = PriorityOptionsModel(
        Today(),
        listOf(
            DefaultOption.Auto,
            DefaultOption.Custom,
            TimeOfDayOption.Morning,
            TimeOfDayOption.Evening
        )
    )

    MoreStuffTheme(darkTheme = true) {
        PriorityOptions(
            model = options
        ) {}
    }
}

@Preview
@Composable
fun PriorityOptionsPreview() {
    val options = PriorityOptionsModel(
        Today(),
        listOf(
            DefaultOption.Auto,
            DefaultOption.Custom,
            TimeOfDayOption.Morning,
            TimeOfDayOption.Evening
        )
    )

    MoreStuffTheme() {
        PriorityOptions(
            model = options
        ) {}
    }
}

