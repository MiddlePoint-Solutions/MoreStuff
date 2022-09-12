package co.softov.morestuff.android.ui.main.input

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import co.softov.morestuff.android.domain.enums.DefaultOption
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.enums.TimeOfDayOption
import co.softov.morestuff.android.presentation.PriorityOptionsModel
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max


@Composable
fun PriorityOptions(
    modifier: Modifier = Modifier,
    optionsFlow: StateFlow<PriorityOptionsModel>,
    onPrioritySelected: (Priority) -> Unit
) {

    val maxItemsInRow = 3

    val model by optionsFlow.collectAsState()
    val options = model.options

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
            .animateContentSize(animationSpec = tween())
    ) {
        if (secondRow.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                secondRow.forEach {
                    PriorityButton(onSelected = { /*TODO*/ }, text = it.toString())
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            firstRow.forEach {
                PriorityButton(onSelected = { /*TODO*/ }, text = it.toString())
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

@Composable
private fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 2,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 } // Keep track of the width of each row
        val rowHeights = IntArray(rows) { 0 } // Keep track of the height of each row

        // Don't constrain child views further, measure them with given constraints
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
            ?: constraints.minWidth
        // Grid's height is the sum of each row
        val height = rowHeights.sum().coerceIn(constraints.minHeight, constraints.maxHeight)

        // y co-ord of each row
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }
        layout(width, height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.place(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}
