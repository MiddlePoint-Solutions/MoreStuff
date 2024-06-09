package io.middlepoint.morestuff.shared.ui.components.priority

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.middlepoint.morestuff.shared.ui.model.PriorityUiModel
import morestuff.shared.generated.resources.Res
import morestuff.shared.generated.resources.priority_later
import morestuff.shared.generated.resources.priority_now
import morestuff.shared.generated.resources.priority_plan
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrioritySelector(
    priority: PriorityUiModel,
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
            derivedStateOf { priority is PriorityUiModel.Now }
        }

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = nowSelected,
            onClick = onNowSelected,
        ) {
            Text(
                text = stringResource(Res.string.priority_now),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        val laterSelected by remember(priority) {
            derivedStateOf { priority is PriorityUiModel.Later }
        }

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = laterSelected,
            onClick = onLaterSelected,
        ) {
            Text(
                text = stringResource(Res.string.priority_later),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        val planSelected by remember(priority) {
            derivedStateOf { priority is PriorityUiModel.Plan }
        }

        PriorityButton(
            modifier = Modifier
                .width(110.dp)
                .height(47.dp),
            selected = planSelected,
            onClick = onPlanSelected,
        ) {
            Text(
                text = stringResource(Res.string.priority_plan),
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

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light",
//    backgroundColor = 0xFFFFFFFF
//)
//@Composable
//fun UserPriorityInputPreviewDark() {
//    MoreStuffTheme {
//        PrioritySelector(
//            priority = PriorityUiModel.Now,
//        )
//    }
//}