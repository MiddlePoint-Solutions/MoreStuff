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
import co.softov.morestuff.android.ui.home.PriorityUI.*
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun PrioritySelector(
    priority: PriorityUI,
    onPrioritySelected: (PriorityUI) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 8.dp
    ) {
        Row {

            PriorityButton(
                modifier = Modifier
                    .weight(0.34f)
                    .height(50.dp),
                selected = Now == priority,
                onSelected = { onPrioritySelected(Now) },
                text = stringResource(id = R.string.priority_now)
            )

            PriorityButton(
                modifier = Modifier
                    .weight(0.34f)
                    .height(50.dp),
                selected = Later == priority,
                onSelected = { onPrioritySelected(Later) },
                text = stringResource(id = R.string.priority_later)
            )

            PriorityButton(
                modifier = Modifier
                    .weight(0.34f)
                    .height(50.dp),
                selected = Plan == priority,
                onSelected = { onPrioritySelected(Plan) },
                text = stringResource(id = R.string.priority_plan)
            )

        }
    }
}

@Preview
@Composable
fun UserPriorityInputPreviewDark() {
    MoreStuffTheme() {
        PrioritySelector(
            priority = Now,
            onPrioritySelected = {}
        )
    }
}

@Preview
@Composable
fun UserPriorityInputPreview() {
    MoreStuffTheme {
        PrioritySelector(
            priority = Now,
            onPrioritySelected = {}
        )
    }
}