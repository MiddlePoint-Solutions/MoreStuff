package io.middlepoint.morestuff.shared.ui.screen.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.middlepoint.morestuff.shared.data.utils.toUuid
import io.middlepoint.morestuff.shared.domain.model.Shareable
import io.middlepoint.morestuff.shared.domain.model.Uuid
import io.middlepoint.morestuff.shared.domain.navigation.AppRoute
import io.middlepoint.morestuff.shared.domain.navigation.Home
import io.middlepoint.morestuff.shared.domain.navigation.ImagePreview
import io.middlepoint.morestuff.shared.domain.navigation.Import
import io.middlepoint.morestuff.shared.domain.navigation.Review
import io.middlepoint.morestuff.shared.domain.navigation.ScopeScreen
import io.middlepoint.morestuff.shared.domain.navigation.Scopes
import io.middlepoint.morestuff.shared.domain.navigation.SettingScreen.AboutLibraries
import io.middlepoint.morestuff.shared.domain.navigation.SettingScreen.Developer
import io.middlepoint.morestuff.shared.domain.navigation.SettingScreen.Root
import io.middlepoint.morestuff.shared.domain.navigation.Settings
import io.middlepoint.morestuff.shared.domain.navigation.SignIn
import io.middlepoint.morestuff.shared.domain.navigation.SignInEmail
import io.middlepoint.morestuff.shared.domain.navigation.TaskChat
import io.middlepoint.morestuff.shared.domain.navigation.TaskChatImageImport
import io.middlepoint.morestuff.shared.domain.navigation.TaskChatImagePreview
import io.middlepoint.morestuff.shared.domain.navigation.rememberNavBackStack
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
import org.koin.core.Koin


@Composable
fun MoreStuffNavHost(
  startDestination: AppRoute,
  shareContent: (taskId: Uuid, content: Shareable) -> Unit,
) {

  val koin = getKoin()

  val appBackStack = rememberNavBackStack<AppRoute>(startDestination)

  CompositionLocalProvider(
    LocalScreenSize provides getScreenSizeInfo(),
  ) {

    NavDisplay(
      backStack = appBackStack,
      entryProvider = entryProvider {
        screens(appBackStack, koin, shareContent)
      },
      transitionSpec = {
        // Slide in from right when navigating forward
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
      },
      popTransitionSpec = {
        // Slide in from left when navigating back
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
      },
      predictivePopTransitionSpec = {
        // Slide in from left when navigating back
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
      },
      entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
      )
    )
  }
}

private fun EntryProviderScope<AppRoute>.screens(
  backStack: NavBackStack<AppRoute>,
  koin: Koin,
  shareContent: (taskId: Uuid, content: Shareable) -> Unit,
) {


  // navigation2 — keep for reference
  // composable<SignIn>(
  //   enterTransition = { fadeIn() },
  //   exitTransition = { null },
  // ) { backStackEntry ->
  //   SignInScreen(
  //     onNext = {
  //       navController.navigate(Home) {
  //         popUpTo(startDestination) { inclusive = true }
  //       }
  //     },
  //     onSignInWithEmail = { navController.navigate(SignInEmail) }
  //   )
  // }
  entry<SignIn> {
    SignInScreen(
      onNext = {
        backStack.clear()
        backStack.add(Home)
      },
      onSignInWithEmail = { backStack.add(SignInEmail) }
    )
  }

  // navigation2 — keep for reference
  // animatedComposable<SignInEmail> {
  //   SignInEmailScreen(
  //     onNext = {
  //       navController.navigate(Home) {
  //         popUpTo(startDestination) { inclusive = true }
  //       }
  //     }
  //   )
  // }
  entry<SignInEmail> {
    SignInEmailScreen(
      onNext = {
        backStack.clear()
        backStack.add(Home)
      }
    )
  }

  // navigation2 — keep for reference
  // composable<Home> {
  //   HomeScreen(
  //     navigateToSettings = { navController.navigate(Settings) },
  //     navigateToTaskChat = { taskId -> navController.navigate(TaskChat(taskId.value)) }
  //   )
  // }
  entry<Home> {
    HomeScreen(
      navigateToSettings = { backStack.add(Settings) },
      navigateToTaskChat = { taskId -> backStack.add(TaskChat(taskId.value)) }
    )
  }

  // navigation2 — keep for reference
  // composable<Review> { backStackEntry ->
  //   val screen = backStackEntry.toRoute<Review>()
  //   ReviewScreen(
  //     onBack = { navController.popBackStack() },
  //     initialScopeId = Uuid(screen.scopeId)
  //   )
  // }
  entry<Review> { screen ->
    ReviewScreen(
      onBack = { backStack.removeLastOrNull() },
      initialScopeId = Uuid(screen.scopeId)
    )
  }

  // navigation2 — keep for reference
  // navigation<Settings>(Root) {
  //   animatedComposable<Root> {
  //     val viewModel = viewModel {
  //       SettingsViewModel(
  //         store = koin.get(),
  //         getApiKeyUseCase = koin.get()
  //       )
  //     }
  //     val model by viewModel.models.collectAsState()
  //     SettingsContent(
  //       onBack = { navController.popBackStack() },
  //       model = model,
  //       selectAppTheme = { index -> viewModel.take(SettingsEvent.SelectAppTheme(index)) },
  //       selectLanguage = { index -> viewModel.take(SettingsEvent.SelectLanguage(index)) },
  //       enableDevSettings = { viewModel.take(SettingsEvent.EnableDevSettings(true)) },
  //       showDevSettings = { navController.navigate(Developer) },
  //       showScopesSettings = { navController.navigate(Scopes) },
  //       showLibraries = { navController.navigate(AboutLibraries) },
  //       signOut = { viewModel.take(SettingsEvent.SignOut) },
  //       openAppSettings = { viewModel.take(SettingsEvent.OpenAppSettings) },
  //       setApiKey = { apiKey -> viewModel.take(SettingsEvent.SetApiKey(apiKey)) }
  //     )
  //   }
  //   animatedComposable<Developer> {
  //     val viewModel = viewModel {
  //       SettingsViewModel(
  //         store = koin.get(),
  //         getApiKeyUseCase = koin.get()
  //       )
  //     }
  //     DevSettingsScreen(
  //       onBack = { navController.popBackStack() },
  //       onDevSettingsDisabled = { viewModel.take(SettingsEvent.EnableDevSettings(false)) }
  //     )
  //   }
  //   animatedComposable<AboutLibraries> {
  //     AboutLibrariesScreen(onBack = { navController.popBackStack() })
  //   }
  // }
  entry<Root> {
    val viewModel = viewModel {
      SettingsViewModel(
        store = koin.get(),
        getApiKeyUseCase = koin.get()
      )
    }
    val model by viewModel.models.collectAsState()

    SettingsContent(
      onBack = { backStack.removeLastOrNull() },
      model = model,
      selectAppTheme = { index -> viewModel.take(SettingsEvent.SelectAppTheme(index)) },
      selectLanguage = { index -> viewModel.take(SettingsEvent.SelectLanguage(index)) },
      enableDevSettings = { viewModel.take(SettingsEvent.EnableDevSettings(true)) },
      showDevSettings = { backStack.add(Developer) },
      showScopesSettings = { backStack.add(Scopes) },
      showLibraries = { backStack.add(AboutLibraries) },
      signOut = { viewModel.take(SettingsEvent.SignOut) },
      openAppSettings = { viewModel.take(SettingsEvent.OpenAppSettings) },
      setApiKey = { apiKey -> viewModel.take(SettingsEvent.SetApiKey(apiKey)) }
    )
  }

  entry<Developer> {
    val viewModel = viewModel {
      SettingsViewModel(
        store = koin.get(),
        getApiKeyUseCase = koin.get()
      )
    }

    DevSettingsScreen(
      onBack = { backStack.removeLastOrNull() },
      onDevSettingsDisabled = { viewModel.take(SettingsEvent.EnableDevSettings(false)) }
    )
  }

  entry<AboutLibraries> {
    AboutLibrariesScreen(onBack = { backStack.removeLastOrNull() })
  }

  // navigation2 — keep for reference
  // navigation<Scopes>(ScopeScreen.Root) {
  //   animatedComposable<ScopeScreen.Root> {
  //     val viewModel = viewModel { ScopesViewModel() }
  //     val model by viewModel.models.collectAsState()
  //     ScopesContent(
  //       model = model,
  //       onBack = { navController.popBackStack() },
  //       onCreateScope = { navController.navigate(ScopeScreen.Create) },
  //       onEditScope = { scope ->
  //         navController.navigate(ScopeScreen.Edit(scope.id.value))
  //       },
  //       onEvent = viewModel::take
  //     )
  //   }
  //   animatedComposable<ScopeScreen.Create> {
  //     val viewModel = viewModel<ScopesViewModel>()
  //     CreateScopeScreen(
  //       onBack = { navController.popBackStack() },
  //       onSaveScope = { title ->
  //         viewModel.take(ScopesUiEvent.CreateScope(title))
  //         navController.popBackStack()
  //       }
  //     )
  //   }
  //   animatedComposable<ScopeScreen.Edit> { backStackEntry ->
  //     val viewModel = viewModel<ScopesViewModel>()
  //     val model by viewModel.models.collectAsState()
  //     val screen = backStackEntry.toRoute<ScopeScreen.Edit>()
  //     EditScopeScreen(
  //       scope = model.scopes.first { it.id.value == screen.scopeId },
  //       onBack = { navController.popBackStack() },
  //       onSaveScope = { title ->
  //         viewModel.take(UpdateScopeName(screen.scopeId.toUuid(), title))
  //         navController.popBackStack()
  //       }
  //     )
  //   }
  // }
  entry<ScopeScreen.Root> {
    val viewModel = viewModel { ScopesViewModel() }
    val model by viewModel.models.collectAsState()

    ScopesContent(
      model = model,
      onBack = { backStack.removeLastOrNull() },
      onCreateScope = { backStack.add(ScopeScreen.Create) },
      onEditScope = { scope -> backStack.add(ScopeScreen.Edit(scope.id.value)) },
      onEvent = viewModel::take
    )
  }

  entry<ScopeScreen.Create> {
    val viewModel = viewModel<ScopesViewModel>()

    CreateScopeScreen(
      onBack = { backStack.removeLastOrNull() },
      onSaveScope = { title ->
        viewModel.take(ScopesUiEvent.CreateScope(title))
        backStack.removeLastOrNull()
      }
    )
  }

  entry<ScopeScreen.Edit> { screen ->
    val viewModel = viewModel<ScopesViewModel>()
    val model by viewModel.models.collectAsState()

    EditScopeScreen(
      scope = model.scopes.first { it.id.value == screen.scopeId },
      onBack = { backStack.removeLastOrNull() },
      onSaveScope = { title ->
        viewModel.take(UpdateScopeName(screen.scopeId.toUuid(), title))
        backStack.removeLastOrNull()
      }
    )
  }

  // navigation2 — keep for reference
  // animatedComposable<TaskChat> { backStackEntry ->
  //   val screen = backStackEntry.toRoute<TaskChat>()
  //   val scope = rememberCoroutineScope()
  //   val viewModel = viewModel(
  //     key = "TaskChat-${screen.taskId}",
  //   ) {
  //     TaskChatViewModel(
  //       taskId = Uuid(screen.taskId)
  //     )
  //   }
  //   val model by viewModel.models.collectAsState()
  //   val isAiEnabled = model.isAIEnabled
  //   val chatActions = remember {
  //     ChatActions(
  //       scheduleAction = { scheduleId, replyType ->
  //         viewModel.take(ScheduleResponse(scheduleId, replyType))
  //       },
  //       copyMessage = { viewModel.take(CopyText(it.content)) },
  //       deleteMessage = { viewModel.take(DeleteMessage(it)) },
  //       onImageSelected = {
  //         val path = it.messageExtra?.url ?: ""
  //         val title = it.content
  //         navController.navigate(TaskChatImagePreview(screen.taskId, path, title))
  //       },
  //       onPdfSelected = {
  //         val path = it.messageExtra?.url ?: ""
  //         viewModel.take(OpenDocument(path))
  //       },
  //       shareImage = { viewModel.take(ShareImage(it)) },
  //       sharePdf = { viewModel.take(ShareDocument(it)) },
  //       shareMessage = { viewModel.take(ShareMessage(it)) },
  //       setEditingMessage = { messageId ->
  //         viewModel.take(SetEditingMessage(messageId))
  //       },
  //       updateMessageContent = { content ->
  //         viewModel.take(UpdateMessageContent(content))
  //       },
  //       isMessageBeingEdited = { messageId ->
  //         model.editingMessageId == messageId
  //       }
  //     )
  //   }
  //   TaskChatContent(
  //     model = model,
  //     onEvent = viewModel::take,
  //     chatActions = chatActions,
  //     onBack = { navController.popBackStack() },
  //     sendTaskMessage = {
  //       viewModel.take(InputText(it))
  //       if (isAiEnabled) viewModel.take(CreateAIMessage(it))
  //     },
  //     imagePicked = { scope.launch { navController.navigate(TaskChatImageImport(it.toString())) } },
  //     pdfPicked = { viewModel.take(InputDocument(it, title = "")) },
  //     isAIEnabled = isAiEnabled
  //   )
  // }
  entry<TaskChat> { screen ->
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
          backStack.add(TaskChatImagePreview(screen.taskId, path, title))
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
      onBack = { backStack.removeLastOrNull() },
      sendTaskMessage = {
        viewModel.take(InputText(it))
        if (isAiEnabled) viewModel.take(CreateAIMessage(it))
      },
      imagePicked = { scope.launch { backStack.add(TaskChatImageImport(it.toString())) } },
      pdfPicked = { viewModel.take(InputDocument(it, title = "")) },
      isAIEnabled = isAiEnabled
    )
  }

  // TODO — navigation2 (kept for reference)
  // composable<ChatScreen.Import> { backStackEntry ->
  //   val screen = backStackEntry.toRoute<ChatScreen.Import>()
  //   ImageImportScreen(
  //     image = screen.imageFile,
  //     onImport = { title ->
  //       viewModel.take(InputUserMedia(screen.imageFile, title))
  //       navController.popBackStack()
  //     },
  //     onBack = { navController.popBackStack() }
  //   )
  // }

  // navigation2 — keep for reference
  // composable<TaskChatImagePreview> { backStackEntry ->
  //   val screen = backStackEntry.toRoute<TaskChatImagePreview>()
  //   val viewModel = viewModel(
  //     key = "TaskChat-${screen.taskId}",
  //   ) {
  //     TaskChatViewModel(
  //       taskId = Uuid(screen.taskId)
  //     )
  //   }
  //   ImagePreviewScreen(
  //     imagePath = screen.imagePath,
  //     onBack = { navController.popBackStack() },
  //     onSendImage = { viewModel.take(ShareImage(screen.imagePath)) },
  //     title = screen.title,
  //   )
  // }
  entry<TaskChatImagePreview> { screen ->
    val viewModel = viewModel(
      key = "TaskChat-${screen.taskId}",
    ) {
      TaskChatViewModel(
        taskId = Uuid(screen.taskId)
      )
    }

    ImagePreviewScreen(
      imagePath = screen.imagePath,
      onBack = { backStack.removeLastOrNull() },
      onSendImage = { viewModel.take(ShareImage(screen.imagePath)) },
      title = screen.title,
    )
  }

  // navigation2 — keep for reference
  // composable<ImagePreview> { backStackEntry ->
  //   val screen = backStackEntry.toRoute<ImagePreview>()
  //   val imageFile = remember { createKmpFile(screen.imageUri) }
  //   logger.d { " shareable imageFile: $imageFile" }
  //   ImageImportScreen(
  //     image = imageFile,
  //     onImport = { message ->
  //       val shareableImage = Shareable.Image(screen.imageUri, message)
  //       logger.d { "shareableImage: $shareableImage" }
  //       shareContent(Uuid(screen.taskId), shareableImage)
  //       navController.navigate(TaskChat(screen.taskId)) {
  //         popUpTo(Home)
  //       }
  //     },
  //     onBack = { navController.popBackStack() }
  //   )
  // }
  entry<ImagePreview> { screen ->
    val imageFile = remember { createKmpFile(screen.imageUri) }
    logger.d { " shareable imageFile: $imageFile" }

    ImageImportScreen(
      image = imageFile,
      onImport = { message ->
        val shareableImage = Shareable.Image(screen.imageUri, message)
        logger.d { "shareableImage: $shareableImage" }
        shareContent(Uuid(screen.taskId), shareableImage)
        backStack.clear()
        backStack.add(Home)
        backStack.add(TaskChat(screen.taskId))
      },
      onBack = { backStack.removeLastOrNull() }
    )
  }

  // navigation2 — keep for reference
  // composable<Import.Text> { backStackEntry ->
  //   val screen = backStackEntry.toRoute<Import.Text>()
  //   ImportScreen(
  //     onBack = { navController.popBackStack() },
  //     shareable = Shareable.Text(screen.message),
  //   ) { taskId, shareable ->
  //     shareContent(taskId, shareable)
  //     navController.navigate(
  //       route = TaskChat(taskId.value),
  //       navOptions = navOptions { popUpTo(Home) }
  //     )
  //   }
  // }
  entry<Import.Text> { screen ->
    ImportScreen(
      onBack = { backStack.removeLastOrNull() },
      shareable = Shareable.Text(screen.message),
    ) { taskId, shareable ->
      shareContent(taskId, shareable)
      backStack.clear()
      backStack.add(Home)
      backStack.add(TaskChat(taskId.value))
    }
  }

  // navigation2 — keep for reference
  // composable<Import.Image> { backStackEntry ->
  //   val screen = backStackEntry.toRoute<Import.Image>()
  //   ImportScreen(
  //     onBack = { navController.popBackStack() },
  //     shareable = Shareable.Image(screen.uri, ""),
  //   ) { taskId, shareable ->
  //     navController.navigate(ImagePreview(screen.uri, taskId.value))
  //   }
  // }
  entry<Import.Image> { screen ->
    ImportScreen(
      onBack = { backStack.removeLastOrNull() },
      shareable = Shareable.Image(screen.uri, ""),
    ) { taskId, _ ->
      backStack.add(ImagePreview(screen.uri, taskId.value))
    }
  }
}
