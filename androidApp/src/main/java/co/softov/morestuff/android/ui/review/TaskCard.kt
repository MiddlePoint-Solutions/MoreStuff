package co.softov.morestuff.android.ui.review

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
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
    isVisible: Boolean = false,
    isClickable: Boolean = true,
    showTaskChat: (taskId: Long) -> Unit,
) {
    var visibleState by remember { mutableStateOf(true) }
    val selectedState = remember { MutableTransitionState(false) }

    val backgroundColors by remember {
        derivedStateOf { TaskColors.getProfileColorsForTask(task.title) }
    }

    val elevationState = remember { MutableTransitionState(false) }
    val elevationTransition = updateTransition(selectedState, "Selected Transition")
    val elevation by elevationTransition.animateDp(label = "AnimateElevation") {
        if (it) 12.dp else 0.dp
    }
    val extraDetails by remember(task.extraDetails) {
        derivedStateOf { task.extraDetails }
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(1000)
            elevationState.targetState = !elevationState.currentState
        }
    }

    AnimatedVisibility(
        visible = visibleState,
        exit = scaleOut(targetScale = 1.5f) + fadeOut()
    ) {
        Card(
            modifier = modifier
                .aspectRatio(0.7f)
                .clickable {
                    selectedState.targetState = !selectedState.currentState
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
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
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Color area
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 105.dp, end =  20.dp)
                        .size(width = 305.dp, height = 320.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.verticalGradient(backgroundColors)
                        ),

                    ) {
                }
                // Icons
                if (extraDetails) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 277.dp, top = 93.dp)
                    ) {
                        FilledIconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(width = 30.dp, height = 25.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp),

                            ) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = "Notes",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(4.dp)

                            )
                        }
                    }
                }
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SlideAnimation(
                        visibleState = selectedState,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row {
                                FilledIconButton(
                                    onClick = { showTaskChat(task.id) },
                                    modifier = Modifier
                                        .size(70.dp)
                                        .padding(end = 20.dp, top = 15.dp, bottom = 5.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = Color(0xFFC4C5DD),

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
                                    onClick = {
                                        onComplete(task)
                                        // visibleState = false
                                    },
                                    modifier = Modifier
                                        .size(70.dp)
                                        .padding(end = 20.dp, top = 15.dp, bottom = 5.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = stringResource(R.string.cd_task_complete),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
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
                isCompleted = false
            ),
            onComplete = {},
            showTaskChat = {}
        )
    }
}