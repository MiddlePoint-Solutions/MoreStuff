package co.softov.morestuff.android.ui.priority

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.enums.AppTheme
import co.softov.morestuff.android.ui.home.PriorityModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.ProvideAppTheme

@Composable
fun PrioritySelector(
    priority: PriorityModel,
    modifier: Modifier = Modifier,
    onNowSelected: () -> Unit = {},
    onLaterSelected: () -> Unit = {},
    onPlanSelected: () -> Unit = {},
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
                selected = priority is PriorityModel.Now,
                onSelected = onNowSelected,
                text = stringResource(id = R.string.priority_now)
            )

            PriorityButton(
                modifier = Modifier
                    .weight(0.34f)
                    .height(50.dp),
                selected = priority is PriorityModel.Later,
                onSelected = onLaterSelected,
                text = stringResource(id = R.string.priority_later)
            )

            PriorityButton(
                modifier = Modifier
                    .weight(0.34f)
                    .height(50.dp),
                selected = priority is PriorityModel.Plan,
                onSelected = onPlanSelected,
                text = stringResource(id = R.string.priority_plan)
            )

        }
    }
}

@Preview
@Composable
fun UserPriorityInputPreviewDark() {
    ProvideAppTheme(theme = AppTheme.Dark) {
        MoreStuffTheme() {
            PrioritySelector(
                priority = PriorityModel.Now,
            )
        }
    }
}

@Preview
@Composable
fun UserPriorityInputPreview() {
    MoreStuffTheme {
        PrioritySelector(
            priority = PriorityModel.Now,
        )
    }
}