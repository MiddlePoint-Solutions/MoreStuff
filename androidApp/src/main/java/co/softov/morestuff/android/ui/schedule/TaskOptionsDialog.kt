package co.softov.morestuff.android.ui.schedule

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.domain.model.TaskDomain
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskOptionsDialog(
    sheetState: SheetState,
    task: TaskDomain,
    dismissDialog: () -> Unit,
    completeTask: () -> Unit,
    moveToTop: () -> Unit,
    moveToBottom: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = dismissDialog,
        sheetState = sheetState,
    ) {
        TaskOptionItem(
            onClick = completeTask,
            title = {
                Text(
                    text = stringResource(R.string.complete),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.cd_complete_task)
                )
            }
        )
        TaskOptionItem(
            onClick = moveToTop,
            title = {
                Text(
                    text = stringResource(R.string.move_to_top),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 31.sp
                )
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.arrows_more_up_48px),
                    contentDescription = stringResource(R.string.cd_move_to_top),
                    modifier = Modifier.size(24.dp),
                )
            }
        )
        TaskOptionItem(
            onClick = moveToBottom,
            title = {
                Text(
                    text = stringResource(R.string.move_to_bottom),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.arrows_more_down_48px),
                    contentDescription = stringResource(R.string.cd_move_task_to_bottom),
                    modifier = Modifier.size(24.dp),
                )
            }
        )
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun TaskOptionItem(
    onClick: () -> Unit,
    title: @Composable () -> Unit,
    icon: @Composable () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(10.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        title()
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
private fun Preview() {
    MoreStuffTheme {
        TaskOptionItem(
            onClick = {  },
            title = {
                Text(
                    text = stringResource(R.string.complete),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.cd_complete_task)
                )
            }
        )
    }
}