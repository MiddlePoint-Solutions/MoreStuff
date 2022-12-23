@file:OptIn(ExperimentalSwipeableCardApi::class)

package co.softov.morestuff.android.ui.review

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.softov.morestuff.android.R
import co.softov.morestuff.android.ui.list.model.ScheduleListItemViewModel
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import org.koin.androidx.compose.getViewModel

@Composable
fun ReviewContent(
    modifier: Modifier = Modifier,
    viewModel: ReviewViewModel = getViewModel()
) {

    val model by viewModel.model.collectAsState()

    Box(modifier = modifier) {
        val states = model.items
            .map { it to rememberSwipeableCardState() }
        var hint by remember {
            mutableStateOf("Swipe a card or press a button below")
        }

        Hint(hint)

        Box(
            modifier
                .padding(24.dp)
                .fillMaxSize()
                .aspectRatio(1f)
                .align(Alignment.Center)
        ) {
            states.forEach { (schedule, state) ->
                if (state.swipedDirection == null) {
                    ProfileCard(
                        modifier = modifier
                            .fillMaxSize()
                            .swipableCard(
                                state = state,
                                blockedDirections = listOf(Direction.Down),
                                onSwiped = {
                                    // swipes are handled by the LaunchedEffect
                                    // so that we track button clicks & swipes
                                    // from the same place
                                },
                                onSwipeCancel = {
                                    Log.d("Swipeable-Card", "Cancelled swipe")
                                    hint = "You canceled the swipe"
                                }
                            ),
                        schedule = schedule
                    )
                }
//                LaunchedEffect(profile, state.swipedDirection) {
//                    if (state.swipedDirection != null) {
//                        hint = "You swiped ${stringFrom(state.swipedDirection!!)}"
//                    }
//                }
            }
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
//            CircleButton(
//                onClick = {
//                    scope.launch {
//                        val last = states.reversed()
//                            .firstOrNull {
//                                it.second.offset.value == Offset(0f, 0f)
//                            }?.second
//                        last?.swipe(Direction.Left)
//                    }
//                },
//                icon = Icons.Rounded.Close
//            )
//            CircleButton(
//                onClick = {
//                    scope.launch {
//                        val last = states.reversed()
//                            .firstOrNull {
//                                it.second.offset.value == Offset(0f, 0f)
//                            }?.second
//
//                        last?.swipe(Direction.Right)
//                    }
//                },
//                icon = Icons.Rounded.Favorite
//            )
        }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .size(56.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        onClick = onClick
    ) {
        Icon(
            icon, null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier,
    schedule: ScheduleListItemViewModel,
) {
    Card(modifier) {
        Box {
            Column(Modifier.align(Alignment.Center)) {
                Text(
                    text = schedule.taskTitle,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun stringFrom(direction: Direction): String {
    return when (direction) {
        Direction.Left -> "Left 👈"
        Direction.Right -> "Right 👉"
        Direction.Up -> "Up 👆"
        Direction.Down -> "Down 👇"
    }
}

@Composable
fun Scrim(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .height(180.dp)
            .fillMaxWidth()
    )
}

data class MatchProfile(
    val name: String,
    @DrawableRes val drawableResId: Int,
)

val profiles = listOf(
    MatchProfile("Erlich Bachman", R.drawable.ic_launcher_round),
    MatchProfile("Richard Hendricks", R.drawable.ic_launcher_round),
    MatchProfile("Laurie Bream", R.drawable.ic_launcher_round),
    MatchProfile("Russ Hanneman", R.drawable.ic_launcher_round),
    MatchProfile("Dinesh Chugtai", R.drawable.ic_launcher_round),
    MatchProfile("Monica Hall", R.drawable.ic_launcher_round),
    MatchProfile("Bertram Gilfoyle", R.drawable.ic_launcher_round),

    MatchProfile("Peter Gregory", R.drawable.ic_launcher_round),
    MatchProfile("Jared Dunn", R.drawable.ic_launcher_round),
    MatchProfile("Nelson Bighetti", R.drawable.ic_launcher_round),
    MatchProfile("Gavin Belson", R.drawable.ic_launcher_round),
    MatchProfile("Jian Yang", R.drawable.ic_launcher_round),
    MatchProfile("Jack Barker", R.drawable.ic_launcher_round),
)