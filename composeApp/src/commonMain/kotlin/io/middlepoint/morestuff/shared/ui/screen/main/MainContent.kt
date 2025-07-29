package io.middlepoint.morestuff.shared.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.nav.Home
import io.middlepoint.morestuff.shared.domain.nav.ImagePreview
import io.middlepoint.morestuff.shared.domain.nav.Review
import io.middlepoint.morestuff.shared.domain.nav.Scopes
import io.middlepoint.morestuff.shared.domain.nav.Settings
import io.middlepoint.morestuff.shared.domain.nav.Share
import io.middlepoint.morestuff.shared.domain.nav.ShareableNavType
import io.middlepoint.morestuff.shared.domain.nav.SignIn
import io.middlepoint.morestuff.shared.domain.nav.SignInEmail
import io.middlepoint.morestuff.shared.domain.nav.TaskChat
import io.middlepoint.morestuff.shared.platform.createKmpFile
import io.middlepoint.morestuff.shared.ui.local.LocalScreenSize
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatScreen
import io.middlepoint.morestuff.shared.ui.screen.home.HomeScreen
import io.middlepoint.morestuff.shared.ui.screen.image.ImageImportScreen
import io.middlepoint.morestuff.shared.ui.screen.image.logger
import io.middlepoint.morestuff.shared.ui.screen.onboarding.SignInEmailScreen
import io.middlepoint.morestuff.shared.ui.screen.onboarding.SignInScreen
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.CreateScopeScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesScreen
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsScreen
import io.middlepoint.morestuff.shared.ui.screen.share.ShareScreen
import io.middlepoint.morestuff.shared.ui.utils.getScreenSizeInfo
import kotlin.reflect.typeOf

@Composable
fun MainContent(
  navController: NavHostController,
  shareContent: (taskId: Uuid, content: Shareable) -> Unit,
) {
  CompositionLocalProvider(
    LocalScreenSize provides getScreenSizeInfo(),
  ) {
    NavHost(
      navController = navController,
      startDestination = Home,
    ) {
      composable<SignIn> { backStackEntry ->
        val screen = backStackEntry.toRoute<SignIn>()
        SignInScreen(
          isOldUser = screen.isOldUser,
          onNext = { navController.navigate(Home) },
          onSignInWithEmail = { navController.navigate(SignInEmail) }
        )
      }

      composable<SignInEmail> {
        SignInEmailScreen(
          onNext = { navController.navigate(Home) }
        )
      }

      composable<Home> {
        HomeScreen(
          navigateToSettings = { navController.navigate(Settings) },
          navigateToTaskChat = { taskId -> navController.navigate(TaskChat(taskId.value)) }
        )
      }

      composable<Review> { backStackEntry ->
        val screen = backStackEntry.toRoute<Review>()
        ReviewScreen(
          onBack = { navController.popBackStack() },
          initialScopeId = Uuid(screen.scopeId)
        )
      }

      composable<Settings> {
        SettingsScreen(onBack = { navController.popBackStack() })
      }

      composable<Scopes> {
        ScopesScreen(onBack = { navController.popBackStack() })
      }

//      composable<CreateScope> { backStackEntry ->
//        val screen = backStackEntry.toRoute<CreateScope>()
//        CreateScopeScreen(
//          onBack = { navController.popBackStack() },
//          onSaveScope = screen.onSave
//        )
//      }

      composable<TaskChat> { backStackEntry ->
        val screen = backStackEntry.toRoute<TaskChat>()

        TaskChatScreen(
          taskId = Uuid(screen.taskId),
          onBack = { navController.popBackStack() },
        )
      }

      composable<ImagePreview> { backStackEntry ->
        val screen = backStackEntry.toRoute<ImagePreview>()
        val imageFile = remember { createKmpFile(screen.imageUri) }
        logger.d { " shareable imageFile: $imageFile" }
        ImageImportScreen(
          image = imageFile,
          onImport = { message ->
            val shareableImage = Shareable.Image(screen.imageUri, message)
            logger.d { "shareableImage: $shareableImage" }
            shareContent(Uuid(screen.taskId), shareableImage)
            navController.navigate(TaskChat(screen.taskId)) {
              popUpTo(Home)
            }
          },
          onBack = { navController.popBackStack() }
        )
      }

      composable<Share>(
        typeMap = mapOf(typeOf<Shareable>() to ShareableNavType)
      ) { backStackEntry ->
        val screen = backStackEntry.toRoute<Share>()
        ShareScreen(
          onBack = { navController.popBackStack() },
          shareable = screen.shareable,
        ) { taskId, shareable ->
          if (shareable is Shareable.Image) {
            navController.navigate(ImagePreview(shareable.uri, taskId.value))
          } else {
            navController.navigate(TaskChat(taskId.value))
            shareContent(taskId, shareable)
          }
        }
      }
    }
  }
}