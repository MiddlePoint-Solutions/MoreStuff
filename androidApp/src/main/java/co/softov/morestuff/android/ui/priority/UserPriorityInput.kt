package co.softov.morestuff.android.ui.main.input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.Priority
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun UserPriorityInput(
    modifier: Modifier = Modifier,
    currentPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {

    val priorityButtons = listOf(Priority.Today(), Priority.Tomorrow(), Priority.Later())

    Surface(
        shadowElevation = 8.dp
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

val Priority.title: String
    @Composable get() = when (this) {
        is Priority.Today -> stringResource(id = R.string.priority_today)
        is Priority.Tomorrow -> stringResource(id = R.string.priority_tomorrow)
        is Priority.Later -> stringResource(id = R.string.priority_later)
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