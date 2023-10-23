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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.softov.morestuff.android.ui.compose.SlideAnimation
import co.softov.morestuff.android.ui.model.ReviewItemUiModel
import co.softov.morestuff.android.ui.theme.MoreStuffTheme
import co.softov.morestuff.android.ui.theme.TaskColors
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: ReviewItemUiModel,
    onComplete: (ReviewItemUiModel) -> Unit,
    isVisible: Boolean = false,
    isClickable: Boolean = true,
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
                    .background(
                        brush = Brush.verticalGradient(backgroundColors)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
//                        .background(brush = topScrimBrush())
                    ,
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = task.title,
                        color = Color(0xFF1B1B1F),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight(400)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 26.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                }

                SlideAnimation(
                    visibleState = selectedState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp)
//                            .background(brush = bottomScrimBrush())
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                onComplete(task)
//                                visibleState = false
                            },
                            modifier = Modifier
                                .widthIn(min = 140.dp)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(39, 40, 53, 125)
                            )
                        ) {
                            Text(
                                text = "Done",
                                color = Color.White
                            )
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
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
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
            onComplete = {}
        )
    }
}