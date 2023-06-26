package co.softov.morestuff.android.ui.priority

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.home.PriorityUI
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun PrioritySelector(
    priority: PriorityUI,
    onPrioritySelected: (PriorityUI) -> Unit,
    modifier: Modifier = Modifier,
) {

    val priorityButtons = remember {
        listOf(
            PriorityUI.Now, PriorityUI.Later, PriorityUI.Plan
        )
    }

    Surface(
        modifier = modifier,
        shadowElevation = 8.dp
    ) {
        Row {
            priorityButtons.forEach {
                PriorityButton(
                    modifier = Modifier
                        .weight(0.34f)
                        .height(50.dp),
                    selected = it == priority,
                    onSelected = { onPrioritySelected(it) },
                    text = it.title.toUpperCase(Locale.current)
                )
            }
        }
    }
}

private val PriorityUI.title: String
    @Composable get() = when (this) {
        PriorityUI.Now -> stringResource(id = R.string.priority_now)
        PriorityUI.Later -> stringResource(id = R.string.priority_later)
        PriorityUI.Plan -> stringResource(id = R.string.priority_plan)
    }

@Preview
@Composable
fun UserPriorityInputPreviewDark() {
    MoreStuffTheme(darkTheme = true) {
        PrioritySelector(
            priority = PriorityUI.Now,
            onPrioritySelected = {}
        )
    }
}

@Preview
@Composable
fun UserPriorityInputPreview() {
    MoreStuffTheme {
        PrioritySelector(
            priority = PriorityUI.Now,
            onPrioritySelected = {}
        )
    }
}