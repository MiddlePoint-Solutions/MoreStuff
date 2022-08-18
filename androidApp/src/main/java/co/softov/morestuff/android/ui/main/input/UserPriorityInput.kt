package co.softov.morestuff.android.ui.main.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.domain.enums.Priority
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun UserPriorityInput(
    modifier: Modifier = Modifier,
    currentPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {

    val priorityButtons = listOf(Priority.Today(), Priority.Tomorrow(), Priority.Later())

    Surface(
        elevation = 8.dp
    ) {
        Row {
            priorityButtons.forEach {
                PriorityButton(
                    modifier = Modifier
                        .weight(0.34f)
                        .height(50.dp),
                    selected = it == currentPriority,
                    onSelected = { onPrioritySelected(it) },
                    text = it.title.toUpperCase(Locale.current)
                )
            }
        }
    }
}

@Preview
@Composable
fun UserPriorityInputPreviewDark() {
    MoreStuffTheme(darkTheme = true) {
        UserPriorityInput(
            currentPriority = Priority.Today()
        ) {}
    }
}

@Preview
@Composable
fun UserPriorityInputPreview() {
    MoreStuffTheme {
        UserPriorityInput(
            currentPriority = Priority.Today()
        ) {}
    }
}