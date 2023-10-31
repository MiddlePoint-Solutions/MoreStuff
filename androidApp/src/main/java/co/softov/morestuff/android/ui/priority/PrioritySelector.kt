package co.softov.morestuff.android.ui.priority

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.home.PriorityModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme

@Composable
fun PrioritySelector(
    priority: PriorityModel,
    onNowSelected: () -> Unit = {},
    onLaterSelected: () -> Unit = {},
    onPlanSelected: () -> Unit = {},
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        val nowSelected by remember(priority)  {
            derivedStateOf { priority is PriorityModel.Now }
        }

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = nowSelected,
            onClick = onNowSelected,
        ) {
            Text(
                text = stringResource(id = R.string.priority_now),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        val laterSelected by remember(priority) {
            derivedStateOf { priority is PriorityModel.Later }
        }

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = laterSelected,
            onClick = onLaterSelected,
        ) {
            Text(
                text = stringResource(id = R.string.priority_later),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        val planSelected by remember(priority) {
            derivedStateOf { priority is PriorityModel.Plan }
        }

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = planSelected,
            onClick = onPlanSelected,
        ) {
            Text(
                text = stringResource(id = R.string.priority_plan),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light",
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun UserPriorityInputPreviewDark() {
    MoreStuffTheme {
        PrioritySelector(
            priority = PriorityModel.Now,
        )
    }
}