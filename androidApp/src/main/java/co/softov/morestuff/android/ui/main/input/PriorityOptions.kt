package co.softov.morestuff.android.ui.main.input

import android.widget.CalendarView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.softov.morestuff.android.domain.enums.DefaultOption
import co.softov.morestuff.android.domain.enums.PriorityOption
import co.softov.morestuff.android.domain.enums.TimeOfDayOption
import co.softov.morestuff.android.presentation.PriorityOptionsModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme


@Composable
fun PriorityOptions(
    modifier: Modifier = Modifier,
    model: PriorityOptionsModel,
    onOptionSelected: (PriorityOption) -> Unit,
) {

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

        if (selected == DefaultOption.Custom) {
            AndroidView(
                factory = { CalendarView(it) },
                modifier = Modifier.animateContentSize(animationSpec = tween()),
            )
        }
    }
}

@Preview
@Composable
fun PriorityOptionsPreviewDark() {
    val options = PriorityOptionsModel(
        DefaultOption.Auto,
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
        DefaultOption.Auto,
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

