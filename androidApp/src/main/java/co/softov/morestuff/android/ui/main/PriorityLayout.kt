package co.softov.morestuff.android.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.hypot


@Composable
fun PriorityTask(
    text: String
) {
    Box(contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
        modifier = Modifier
            .background(Color.Red, shape = CircleShape)
            .padding(4.dp)
            .layout() { measurable, constraints ->
                // Measure the composable
                val placeable = measurable.measure(constraints)

                //get the current max dimension to assign width=height
                val currentHeight = placeable.height
                var heightCircle = currentHeight
                if (placeable.width > heightCircle)
                    heightCircle = placeable.width

                //assign the dimension and the center position
                layout(heightCircle, heightCircle) {
                    // Where the composable gets placed
                    placeable.place(0, (heightCircle - currentHeight) / 2)
                }
            }
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = Color.White,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
            //Use a min size for short text.

        )
    }
}

@Composable
fun PriorityLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    Layout(
        modifier = modifier
            .verticalScroll(scrollState),
        content = content
    ) { measurables, constraints ->

        val columns = 3
        val columnWidth = (constraints.maxWidth / columns)
        val minItemSize = (columnWidth * 0.55).toInt()
        val itemConstraints = constraints.copy(maxWidth = columnWidth, minWidth = minItemSize)
        val colHeights = IntArray(columns) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            val colY = IntArray(columns) { 0 }
            var nextX = 0
            var nextY = 0
            var column = 0
            val padding = 8.dp.roundToPx()
            val placedItems = mutableListOf<PlaceableItem>()
            Timber.d("START PLACEMENT")
            placeables.forEachIndexed { index, placeable ->

                val itemToPlace = PlaceableItem(placeable)
                if (nextX + placeable.width > constraints.maxWidth) {
                    nextX = 5
                }

                nextY = colY[column]


                var hasPlace = placedItems.isEmpty()
                while (!hasPlace) {

                    hasPlace = placedItems.all {
                        !isIntersecting(nextX, nextY, it, itemToPlace)
                    }

                    if (!hasPlace) {
                        Timber.d("Intersection detected at x: $nextX, y: $nextY")
                        nextX += 30

                        nextY += when (placeable.height) {
                            in minItemSize..minItemSize + 50 -> (placeable.height * 2)
                            else -> placeable.height / 2
                        }

                        if (nextX + placeable.width > constraints.maxWidth) {
                            column = 0
                            nextX = 0
                            nextY = colY[column]
                        }
                        Timber.d("adjusting nextX: $nextX")
                    }
                }

                Timber.d("Placing item $index at: xCount: $nextX, nextY: $nextY")
                val placedItem = placeItem(
                    itemToPlace,
                    x = nextX,
                    y = nextY
                )


                colY[column] += placeable.height
                nextX += placeable.width
                column = if (column < columns - 1) column + 1 else 0
                placedItems.add(placedItem)
            }
            Timber.d("END PLACEMENT")
        }
    }
}

private fun getPlaceForItem(
    placedItems: MutableList<PlaceableItem>,
    nextX: Int,
    nextY: Int,
    itemToPlace: PlaceableItem,
    columns: Int
): Pair<Int, Int> {
    var nextX1 = nextX
    var nextY1 = nextY

    if (placedItems.size < columns) {
        return Pair(nextX1, nextY1)
    }

    placedItems.chunked(columns).lastOrNull()?.forEachIndexed { i, item ->
        val intersects: Boolean = isIntersecting(nextX1, nextY1, item, itemToPlace)
        if (intersects) {
            Timber.d("Intersecting")
            // TODO: here we can check the last relevant items for intersection
        }
    }
    return Pair(nextX1, nextY1)
}

private fun isIntersecting(
    nextX: Int,
    nextY: Int,
    placedItem: PlaceableItem,
    itemToPlace: PlaceableItem,
): Boolean {
    val xDiff = (placedItem.x - nextX).toDouble()
    val yDiff = (placedItem.y - nextY).toDouble()
    return hypot(xDiff, yDiff) <= ((placedItem.width / 2) + itemToPlace.width / 2)
}

fun Placeable.PlacementScope.placeItem(item: PlaceableItem, x: Int, y: Int): PlaceableItem {
    item.placeable.placeRelative(x, y)
    return item.copy(x = x, y = y)
}

data class PlaceableItem(val placeable: Placeable, val x: Int = 0, val y: Int = 0) {
    val width = placeable.width
    val height = placeable.height

    override fun toString(): String {
        return "x: $x, y: $y, width: $width, height: $height"
    }
}

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns = ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}

val testList = listOf(
    "1111111111111111111111 ",
    "2",
    "234",
    "2345",
    "23456",
    "33333333333333333333",
    "44444444444444",
    "55555555555",
    "66666666666666666",
    "77777777777777777",
    "88888888888888888",
    "12121212121212",
    "99",
)

/*
@Preview
@Composable
fun StaggeredVerticalGridPreview() {

    StaggeredVerticalGrid(
        maxColumnWidth = 110.dp,
        modifier = Modifier.padding(4.dp)
    ) {
        testList.forEach {
            PriorityTask(it)
        }
    }
}
*/

@Preview()
@Composable
fun PriorityLayoutPreview() {
    PriorityLayout(
        modifier = Modifier
            .padding(16.dp)
    ) {
        testList.forEach {
            PriorityTask(it)
        }
    }
}

/*

@Preview
@Composable
fun PriorityTaskPreview() {
    PriorityTask("Hello World 12312412412 hellllloooooo")
}*/
