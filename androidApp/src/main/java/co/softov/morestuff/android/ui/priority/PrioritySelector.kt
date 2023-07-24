package co.softov.morestuff.android.ui.priority

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        PriorityButton(
            modifier = Modifier
                .width(100.dp),
            selected = priority is PriorityModel.Now,
            onClick = onNowSelected,
        ) {
            Text(
                text = stringResource(id = R.string.priority_now),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
            )
        }

        PriorityButton(
            modifier = Modifier
                .width(100.dp),
            selected = priority is PriorityModel.Later,
            onClick = onLaterSelected,
        ) {
            Text(
                text = stringResource(id = R.string.priority_later),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
            )
        }

        PriorityButton(
            modifier = Modifier
                .width(100.dp),
            selected = priority is PriorityModel.Plan,
            onClick = onPlanSelected,
        ) {
            Text(
                text = stringResource(id = R.string.priority_plan),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                )
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