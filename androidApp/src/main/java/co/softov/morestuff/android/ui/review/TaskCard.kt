package co.softov.morestuff.android.ui.review

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.compose.SlideAnimation
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.TaskColors
import co.softov.morestuff.android.ui.theme.surfaceContainer
import kotlinx.coroutines.delay

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: ReviewItemUiModel,
    onComplete: (ReviewItemUiModel) -> Unit,
    isClickable: Boolean = true,
    showTaskChat: (taskId: Long) -> Unit,
) {
    val selectedState = remember { MutableTransitionState(false) }

    val backgroundColors by remember {
        derivedStateOf { TaskColors.getProfileColorsForTask(task.title) }
    }

    val extraDetails by remember(task.extraDetails) {
        derivedStateOf { task.extraDetails }
    }

    Card(
        modifier = modifier
            .clickable(isClickable) {
                selectedState.targetState = !selectedState.currentState
            }
            .aspectRatio(0.7f),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = task.title,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight(400),
                fontSize = 25.sp,
                lineHeight = 28.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp),
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(start = 20.dp, end = 20.dp, top = 5.dp),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brush = Brush.verticalGradient(backgroundColors))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (extraDetails) {
                    Surface(
                        modifier = Modifier.size(width = 30.dp, height = 25.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notes,
                            contentDescription = stringResource(R.string.cd_extra_details),
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visibleState = selectedState,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { showTaskChat(task.id) },
                        modifier = Modifier.size(55.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.chat_bubble_24px),
                            contentDescription = stringResource(R.string.cd_show_task_chat),
                            tint = MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                    FilledIconButton(
                        onClick = { onComplete(task) },
                        modifier = Modifier.size(55.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.cd_task_complete),
                        )
                    }
                }
            }
        }

    }
}


@NonRestartableComposable
@Composable
private fun topScrimBrush() = Brush.verticalGradient(
    listOf(
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0f)
    )
)

@NonRestartableComposable
@Composable
private fun bottomScrimBrush() = Brush.verticalGradient(
    listOf(
        Color.White.copy(alpha = 0f),
        Color.White.copy(alpha = 0.5f)
    )
)

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light"
)
@Composable
fun TaskCardPreview() {
    MoreStuffTheme() {
        TaskCard(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f),
            task = ReviewItemUiModel(
                title = "Hellooooo there",
                createTime = "",
                id = 0,
                priorityScore = 0,
                isCompleted = false,
                extraDetails = true
            ),
            onComplete = {},
            showTaskChat = {}
        )
    }
}