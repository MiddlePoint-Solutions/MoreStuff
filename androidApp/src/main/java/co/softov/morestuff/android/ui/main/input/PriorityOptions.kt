package co.softov.morestuff.android.ui.main.input

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.enums.DefaultOption
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.enums.TimeOfDayOption
import co.softov.morestuff.android.presentation.PriorityOptionsModel
import kotlinx.coroutines.flow.StateFlow


@Composable
fun PriorityOptions(
    modifier: Modifier = Modifier,
    onOptionSelected: (PriorityOption) -> Unit,
    optionsFlow: StateFlow<PriorityOptionsModel>,
) {

    val model by optionsFlow.collectAsState()
    val options = model.options
    val selected = model.current

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
            modifier = Modifier.animateContentSize(animationSpec = tween()),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            firstRow.forEach {
                PriorityButton(
                    onSelected = { onOptionSelected(it) },
                    text = it.toString(),
                    shape = MaterialTheme.shapes.small,
                    selected = it == selected
                )
            }
        }

        if (secondRow.isNotEmpty()) {
            Row(
                modifier = Modifier.animateContentSize(animationSpec = tween()),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                secondRow.forEach {
                    PriorityButton(
                        onSelected = { onOptionSelected(it) },
                        text = it.toString(),
                        shape = MaterialTheme.shapes.small,
                        selected = it == selected
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PriorityOptionsPreviewDark() {
    val items: MutableList<PriorityOption> = TimeOfDayOption.values().toMutableList()
    items.add(DefaultOption.Auto)

//    MoreStuffTheme(darkTheme = true) {
//        PriorityOptions(
//            options = items
//        ) {}
//    }
}

@Preview
@Composable
fun PriorityOptionsPreview() {
    val items: MutableList<PriorityOption> = TimeOfDayOption.values().toMutableList()
    items.add(DefaultOption.Auto)

//    MoreStuffTheme {
//        PriorityOptions(
//            options = items
//        ) {}
//    }
}

