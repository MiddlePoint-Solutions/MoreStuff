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
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = priority is PriorityModel.Now,
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

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = priority is PriorityModel.Later,
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

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = priority is PriorityModel.Plan,
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
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight",
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