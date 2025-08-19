package io.middlepoint.morestuff.shared.ui.screen.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.nav.ChatScreen
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
import io.middlepoint.morestuff.shared.ui.screen.chat.ChatActions
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatContent
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CopyText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.CreateAIMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.DeleteMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.InputText
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.OpenDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ScheduleResponse
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.SetEditingMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareDocument
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareImage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.ShareMessage
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatEvent.UpdateMessageContent
import io.middlepoint.morestuff.shared.ui.screen.chat.task.TaskChatViewModel
import io.middlepoint.morestuff.shared.ui.screen.home.HomeScreen
import io.middlepoint.morestuff.shared.ui.screen.image.ImageImportScreen
import io.middlepoint.morestuff.shared.ui.screen.image.logger
import io.middlepoint.morestuff.shared.ui.screen.onboarding.SignInEmailScreen
import io.middlepoint.morestuff.shared.ui.screen.onboarding.SignInScreen
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesScreen
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsScreen
import io.middlepoint.morestuff.shared.ui.screen.share.ShareScreen
import io.middlepoint.morestuff.shared.ui.utils.getScreenSizeInfo
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
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
      enterTransition = { slideInHorizontally { it } + fadeIn() },
      exitTransition = { slideOutHorizontally { -it } + fadeOut() },
      popEnterTransition = { slideInHorizontally { -it / 2 } + fadeIn() },
      popExitTransition = { slideOutHorizontally { it / 2 } + fadeOut() }
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

      // TODO:
      composable<TaskChat> { backStackEntry ->

        val screen = backStackEntry.toRoute<ChatScreen.Chat>()

        val scope = rememberCoroutineScope()

        val viewModel = koinViewModel<TaskChatViewModel>(
          parameters = { parametersOf(Uuid(screen.taskId)) }
        )

        val model by viewModel.models.collectAsState()
        val isAiEnabled = model.isAIEnabled

        // TODO: move this into TaskChat screen below
        val chatActions = remember {
          ChatActions(
            scheduleAction = { scheduleId, replyType ->
              viewModel.take(ScheduleResponse(scheduleId, replyType))
            },
            copyMessage = { viewModel.take(CopyText(it.content)) },
            deleteMessage = { viewModel.take(DeleteMessage(it)) },
            onImageSelected = {
              val path = it.messageExtra?.url ?: ""
              val title = it.content
              navController.navigate(ChatScreen.Preview(path, title))
            },
            onPdfSelected = {
              val path = it.messageExtra?.url ?: ""
              viewModel.take(OpenDocument(path))
            },
            shareImage = { viewModel.take(ShareImage(it)) },
            sharePdf = { viewModel.take(ShareDocument(it)) },
            shareMessage = { viewModel.take(ShareMessage(it)) },
            setEditingMessage = { messageId ->
              viewModel.take(SetEditingMessage(messageId))
            },
            updateMessageContent = { content ->
              viewModel.take(UpdateMessageContent(content))
            },
            isMessageBeingEdited = { messageId ->
              model.editingMessageId == messageId
            }
          )
        }

        TaskChatContent(
          model = model,
          onEvent = viewModel::take,
          chatActions = chatActions,
          onBack = { navController.popBackStack() },
          sendTaskMessage = {
            viewModel.take(InputText(it))
            if (isAiEnabled) viewModel.take(CreateAIMessage(it))
          },
          imagePicked = { scope.launch { navController.navigate(ChatScreen.Import(it)) } },
          pdfPicked = { viewModel.take(InputDocument(it, title = "")) },
          isAIEnabled = isAiEnabled
        )
      }

      // TODO:
//    composable<ChatScreen.Import> { backStackEntry ->
//      val screen = backStackEntry.toRoute<ChatScreen.Import>()
//      ImageImportScreen(
//        image = screen.imageFile,
//        onImport = { title ->
//          viewModel.take(InputUserMedia(screen.imageFile, title))
//          navController.popBackStack()
//        },
//        onBack = { navController.popBackStack() }
//      )
//    }
      // TODO:
//        composable<ChatScreen.Preview> { backStackEntry ->
//          val screen = backStackEntry.toRoute<ChatScreen.Preview>()
//
//          ImagePreviewScreen(
//            imagePath = screen.imagePath,
//            onBack = { navController.popBackStack() },
//            onSendImage = { viewModel.take(ShareImage(screen.imagePath)) },
//            title = screen.title,
//          )
//        }

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