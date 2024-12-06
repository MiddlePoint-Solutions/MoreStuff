package io.middlepoint.morestuff.shared.ui.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.mohamedrejeb.calf.io.KmpFile
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.middlepoint.morestuff.shared.createKmpFile
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.nav.Screen.CreateScope
import io.middlepoint.morestuff.shared.domain.nav.Screen.Home
import io.middlepoint.morestuff.shared.domain.nav.Screen.ImagePreview
import io.middlepoint.morestuff.shared.domain.nav.Screen.OnBoarding
import io.middlepoint.morestuff.shared.domain.nav.Screen.Review
import io.middlepoint.morestuff.shared.domain.nav.Screen.Scopes
import io.middlepoint.morestuff.shared.domain.nav.Screen.Settings
import io.middlepoint.morestuff.shared.domain.nav.Screen.Share
import io.middlepoint.morestuff.shared.domain.nav.Screen.TaskChat
import io.middlepoint.morestuff.shared.ui.local.LocalAppRouter
import io.middlepoint.morestuff.shared.ui.local.LocalScreenSize
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatScreen
import io.middlepoint.morestuff.shared.ui.screen.home.HomeScreen
import io.middlepoint.morestuff.shared.ui.screen.image.ImageImportScreen
import io.middlepoint.morestuff.shared.ui.screen.onboarding.OnBoardingScreen
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.CreateScopeScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesScreen
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsScreen
import io.middlepoint.morestuff.shared.ui.screen.share.ShareScreen
import io.middlepoint.morestuff.shared.ui.utils.getScreenSizeInfo

@Composable
fun MainContent(
  shareContent: (taskId: Long, content: Shareable) -> Unit,
  onBoardingComplete: () -> Unit,
) {
  CompositionLocalProvider(
    LocalScreenSize provides getScreenSizeInfo(),
  ) {

    val router = LocalAppRouter.current

    RoutedContent(
      router = router,
      modifier = Modifier.fillMaxSize(),
      animation = stackAnimation(slide() + fade())
    ) { screen ->
      when (screen) {

        OnBoarding -> OnBoardingScreen(
          onBoardingComplete = {
            onBoardingComplete()
            router.replaceCurrent(Home)
          }
        )

        Home -> HomeScreen()

        is Review -> ReviewScreen(onBack = router::pop, currentScopeId = screen.scopeId)

        Settings -> SettingsScreen(onBack = router::pop)

        Scopes -> ScopesScreen(onBack = router::pop)

        is CreateScope -> CreateScopeScreen(
          onBack = router::pop,
          onSaveScope = screen.onSave
        )

        is TaskChat -> TaskChatScreen(
          taskId = screen.taskId,
          onBack = router::pop
        )

        is ImagePreview -> {
          val imageFile = remember { createKmpFile(screen.imageUri) }
          ImageImportScreen(
            imagePath = imageFile,
            onImport = { message ->
              val shareableImage = Shareable.Image(screen.imageUri, message)
              shareContent(screen.taskId, shareableImage)
              router.replaceAll(Home, TaskChat(screen.taskId))
            },
            onBack = router::pop
          )
        }

        is Share -> {
          ShareScreen(
            onBack = router::pop,
            shareable = screen.shareable,
          ) { taskId, shareable ->
            if (shareable is Shareable.Image) {
              router.push(ImagePreview(shareable.uri, taskId))
            } else {
              router.replaceCurrent(
                TaskChat(taskId),
                onComplete = {
                  shareContent(taskId, shareable)
                }
              )
            }
          }
        }
      }
    }
  }
}
