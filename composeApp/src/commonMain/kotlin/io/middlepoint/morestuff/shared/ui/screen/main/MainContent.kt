package io.middlepoint.morestuff.shared.ui.screen.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import androidx.navigation.toRoute
import io.middlepoint.morestuff.shared.data.utils.toUuid
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.nav.Home
import io.middlepoint.morestuff.shared.domain.nav.ImagePreview
import io.middlepoint.morestuff.shared.domain.nav.Import
import io.middlepoint.morestuff.shared.domain.nav.Review
import io.middlepoint.morestuff.shared.domain.nav.ScopeScreen
import io.middlepoint.morestuff.shared.domain.nav.Scopes
import io.middlepoint.morestuff.shared.domain.nav.Screen
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.AboutLibraries
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Developer
import io.middlepoint.morestuff.shared.domain.nav.SettingScreen.Root
import io.middlepoint.morestuff.shared.domain.nav.Settings
import io.middlepoint.morestuff.shared.domain.nav.SignIn
import io.middlepoint.morestuff.shared.domain.nav.SignInEmail
import io.middlepoint.morestuff.shared.domain.nav.TaskChat
import io.middlepoint.morestuff.shared.domain.nav.TaskChatImageImport
import io.middlepoint.morestuff.shared.domain.nav.TaskChatImagePreview
import io.middlepoint.morestuff.shared.platform.createKmpFile
import io.middlepoint.morestuff.shared.ui.extension.animatedComposable
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
import io.middlepoint.morestuff.shared.ui.screen.image.ImagePreviewScreen
import io.middlepoint.morestuff.shared.ui.screen.image.logger
import io.middlepoint.morestuff.shared.ui.screen.onboarding.SignInEmailScreen
import io.middlepoint.morestuff.shared.ui.screen.onboarding.SignInScreen
import io.middlepoint.morestuff.shared.ui.screen.review.ReviewScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.CreateScopeScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.EditScopeScreen
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesContent
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesUiEvent
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesUiEvent.UpdateScopeName
import io.middlepoint.morestuff.shared.ui.screen.scopes.ScopesViewModel
import io.middlepoint.morestuff.shared.ui.screen.settings.AboutLibrariesScreen
import io.middlepoint.morestuff.shared.ui.screen.settings.DevSettingsScreen
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsContent
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsEvent
import io.middlepoint.morestuff.shared.ui.screen.settings.SettingsViewModel
import io.middlepoint.morestuff.shared.ui.screen.share.ImportScreen
import io.middlepoint.morestuff.shared.ui.utils.getScreenSizeInfo
import kotlinx.coroutines.launch
import org.koin.compose.getKoin

@Composable
fun MainContent(
  navController: NavHostController,
  startDestination: Screen,
  shareContent: (taskId: Uuid, content: Shareable) -> Unit,
) {

  val koin = getKoin()

  CompositionLocalProvider(
    LocalScreenSize provides getScreenSizeInfo(),
  ) {

    NavHost(
      navController = navController,
      startDestination = startDestination
    ) {

      composable<SignIn>(
        enterTransition = { fadeIn() },
        exitTransition = { null },
      ) { backStackEntry ->
        val screen = backStackEntry.toRoute<SignIn>()
        SignInScreen(
          isOldUser = screen.isOldUser,
          onNext = {
            navController.navigate(Home) {
              popUpTo(startDestination) { inclusive = true }
            }
          },
          onSignInWithEmail = { navController.navigate(SignInEmail) }
        )
      }

      animatedComposable<SignInEmail> {
        SignInEmailScreen(
          onNext = {
            navController.navigate(Home) {
              popUpTo(startDestination) { inclusive = true }
            }
          }
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

      navigation<Settings>(Root) {

        animatedComposable<Root> {

          val viewModel = viewModel {
            SettingsViewModel(
              store = koin.get(),
              getApiKeyUseCase = koin.get()
            )
          }
          val model by viewModel.models.collectAsState()

          SettingsContent(
            // TODO: pass ViewModel::take and a router lambda to reduce the number of properties.
            onBack = { navController.popBackStack() },
            model = model,
            selectAppTheme = { index -> viewModel.take(SettingsEvent.SelectAppTheme(index)) },
            selectLanguage = { index -> viewModel.take(SettingsEvent.SelectLanguage(index)) },
            enableDevSettings = { viewModel.take(SettingsEvent.EnableDevSettings(true)) },
            showDevSettings = { navController.navigate(Developer) },
            showScopesSettings = { navController.navigate(Scopes) },
            showLibraries = { navController.navigate(AboutLibraries) },
            signOut = { viewModel.take(SettingsEvent.SignOut) },
            openAppSettings = { viewModel.take(SettingsEvent.OpenAppSettings) },
            setApiKey = { apiKey -> viewModel.take(SettingsEvent.SetApiKey(apiKey)) }
          )
        }

        animatedComposable<Developer> {

          val viewModel = viewModel {
            SettingsViewModel(
              store = koin.get(),
              getApiKeyUseCase = koin.get()
            )
          }

          DevSettingsScreen(
            onBack = { navController.popBackStack() },
            onDevSettingsDisabled = { viewModel.take(SettingsEvent.EnableDevSettings(false)) }
          )
        }

        animatedComposable<AboutLibraries> {
          AboutLibrariesScreen(onBack = { navController.popBackStack() })
        }
      }

      navigation<Scopes>(ScopeScreen.Root) {

        animatedComposable<ScopeScreen.Root> {

          val viewModel = viewModel { ScopesViewModel() }
          val model by viewModel.models.collectAsState()

          ScopesContent(
            model = model,
            onBack = { navController.popBackStack() },
            onCreateScope = { navController.navigate(ScopeScreen.Create) },
            onEditScope = { scope ->
              navController.navigate(ScopeScreen.Edit(scope.id.value))
            },
            onEvent = viewModel::take
          )
        }

        animatedComposable<ScopeScreen.Create> {

          val viewModel = viewModel<ScopesViewModel>()

          CreateScopeScreen(
            onBack = { navController.popBackStack() },
            onSaveScope = { title ->
              viewModel.take(ScopesUiEvent.CreateScope(title))
              navController.popBackStack()
            }
          )
        }

        animatedComposable<ScopeScreen.Edit> { backStackEntry ->

          val viewModel = viewModel<ScopesViewModel>()
          val model by viewModel.models.collectAsState()

          val screen = backStackEntry.toRoute<ScopeScreen.Edit>()
          EditScopeScreen(
            scope = model.scopes.first { it.id.value == screen.scopeId },
            onBack = { navController.popBackStack() },
            onSaveScope = { title ->
              viewModel.take(UpdateScopeName(screen.scopeId.toUuid(), title))
              navController.popBackStack()
            }
          )
        }

      }

//      composable<CreateScope> { backStackEntry ->
//        val screen = backStackEntry.toRoute<CreateScope>()
//        CreateScopeScreen(
//          onBack = { navController.popBackStack() },
//          onSaveScope = screen.onSave
//        )
//      }


      animatedComposable<TaskChat> { backStackEntry ->

        val screen = backStackEntry.toRoute<TaskChat>()

        val scope = rememberCoroutineScope()

        val viewModel = viewModel(
          key = "TaskChat-${screen.taskId}",
        ) {
          TaskChatViewModel(
            taskId = Uuid(screen.taskId)
          )
        }

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
              navController.navigate(TaskChatImagePreview(screen.taskId, path, title))
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
          imagePicked = { scope.launch { navController.navigate(TaskChatImageImport(it.toString())) } },
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

      composable<TaskChatImagePreview> { backStackEntry ->
        val screen = backStackEntry.toRoute<TaskChatImagePreview>()

        val viewModel = viewModel(
          key = "TaskChat-${screen.taskId}",
        ) {
          TaskChatViewModel(
            taskId = Uuid(screen.taskId)
          )
        }

        ImagePreviewScreen(
          imagePath = screen.imagePath,
          onBack = { navController.popBackStack() },
          onSendImage = { viewModel.take(ShareImage(screen.imagePath)) },
          title = screen.title,
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

      composable<Import.Text> { backStackEntry ->
        val screen = backStackEntry.toRoute<Import.Text>()
        ImportScreen(
          onBack = { navController.popBackStack() },
          shareable = Shareable.Text(screen.message),
        ) { taskId, shareable ->
          shareContent(taskId, shareable)
          navController.navigate(
            route = TaskChat(taskId.value),
            navOptions = navOptions { popUpTo(Home) }
          )
        }
      }

      composable<Import.Image> { backStackEntry ->
        val screen = backStackEntry.toRoute<Import.Image>()
        ImportScreen(
          onBack = { navController.popBackStack() },
          shareable = Shareable.Image(screen.uri, ""),
        ) { taskId, shareable ->
          navController.navigate(ImagePreview(screen.uri, taskId.value))
        }
      }

    }
  }
}

