package io.middlepoint.morestuff.shared.ui.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.ui.model.ReviewItemUiModel
import kotlinx.coroutines.delay
import morestuff.composeapp.generated.resources.Res
import morestuff.composeapp.generated.resources.button_next
import morestuff.composeapp.generated.resources.onboarding_review_subtitle
import morestuff.composeapp.generated.resources.onboarding_review_task1
import morestuff.composeapp.generated.resources.onboarding_review_task10
import morestuff.composeapp.generated.resources.onboarding_review_task2
import morestuff.composeapp.generated.resources.onboarding_review_task3
import morestuff.composeapp.generated.resources.onboarding_review_task4
import morestuff.composeapp.generated.resources.onboarding_review_task5
import morestuff.composeapp.generated.resources.onboarding_review_task6
import morestuff.composeapp.generated.resources.onboarding_review_task7
import morestuff.composeapp.generated.resources.onboarding_review_task8
import morestuff.composeapp.generated.resources.onboarding_review_task9
import morestuff.composeapp.generated.resources.onboarding_review_title
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun OnBoardingReviewScreen(
  nextButtonText: String = stringResource(Res.string.button_next),
  onNext: () -> Unit,
) {

  var tasks = remember { listOf<ReviewItemUiModel>() }
  var showExample by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    val ids = listOf(
      Res.string.onboarding_review_task1,
      Res.string.onboarding_review_task2,
      Res.string.onboarding_review_task3,
      Res.string.onboarding_review_task4,
      Res.string.onboarding_review_task5,
      Res.string.onboarding_review_task6,
      Res.string.onboarding_review_task7,
      Res.string.onboarding_review_task8,
      Res.string.onboarding_review_task9,
      Res.string.onboarding_review_task10,
    )

    val createDates = listOf(
      "2025-03-10",
      "2025-03-11",
      "2025-03-12",
      "2025-03-13",
      "2025-03-14",
      "2025-03-15",
      "2025-03-16",
      "2025-03-17",
      "2025-03-18",
      "2025-03-19"
    )




    tasks = List(ids.size) { index ->
      ReviewItemUiModel(
        id = Uuid("$index"),
        createTime = createDates[index],
        title = getString(ids[index]),
        position = "${index + 1}/${ids.size}",
        priorityScore = 5,
        isCompleted = false,
        extraDetails = false,
        messages = listOf()
      )
    }
  }

  LaunchedEffect(Unit) {
    delay(500)
    showExample = true
  }

  Box(
    modifier = Modifier
      .windowInsetsPadding(WindowInsets.safeContent)
      .fillMaxSize(),
  ) {

    ConstraintLayout(
      modifier = Modifier.fillMaxSize(),
    ) {

      val (image, title, subtitle) = createRefs()

      Text(
        text = stringResource(Res.string.onboarding_review_title),
        modifier = Modifier
          .fillMaxWidth()
          .constrainAs(title) { top.linkTo(parent.top, margin = 40.dp) },
        style = MaterialTheme.typography.headlineMedium.copy(
          fontSize = 40.sp,
          lineHeight = 44.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        ),
        color = MaterialTheme.colorScheme.primary,
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .constrainAs(subtitle) { top.linkTo(title.bottom, margin = 20.dp) },
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = stringResource(Res.string.onboarding_review_subtitle),
          style = MaterialTheme.typography.headlineSmall,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.secondary,
        )
        Text(
          text = "High ⬆\uFE0F Low ⬇\uFE0F More ➡\uFE0F Less ⬅\uFE0F",
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.secondary,
        )
      }

      AnimatedVisibility(
        visible = showExample,
        modifier = Modifier
          .constrainAs(image) { centerTo(parent) }
          .zIndex(1f),
        enter = fadeIn()
      ) {
        OnBoardingReviewCards(
          tasks = tasks,
        )
      }
    }

    OnboardingButton(
      onClick = onNext,
      title = nextButtonText,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 100.dp)
    )
  }
}

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark"
//)
//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "Light"
//)
//@Composable
//private fun Preview() {
//    MoreStuffTheme {
//        OnBoardingReviewScreen("Next") {}
//    }
//}
