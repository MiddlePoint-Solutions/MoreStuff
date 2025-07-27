# Decompose Router Usage Report

This report provides a comprehensive overview of how `decompose-router` is used in the project. This information will be useful for migrating to Compose Navigation.

## Core Components

The following core components from `decompose-router` are used throughout the project:

*   `Router`: The main class for managing the navigation stack.
*   `RouterContext`: The context object for the router, which is provided to the composable tree using `CompositionLocalProvider`.
*   `rememberRouter`: A function used to create and remember a `Router` instance.
*   `RoutedContent`: A composable that displays the content of the current route.
*   `LocalRouterContext`: A `CompositionLocal` that provides the `RouterContext` to the composable tree.

## Platform Integration

The library is set up on Android, iOS, and Desktop, with each platform having its own way of creating and providing the `RouterContext`.

### Android (`MainActivity.kt`)

On Android, the `RouterContext` is created in `MainActivity.kt` using `defaultRouterContext()` and provided to the composable tree using `CompositionLocalProvider`.

```kotlin
val rootRouterContext: RouterContext = defaultRouterContext()
// ...
CompositionLocalProvider(LocalRouterContext provides rootRouterContext) {
  App()
}
```

### iOS (`MainViewController.kt`)

On iOS, the `RouterContext` is created in `MainViewController.kt` and provided to the composable tree using `CompositionLocalProvider`.

```kotlin
val routerContext = // ... create router context
// ...
ComposeUIViewController {
  CompositionLocalProvider(LocalRouterContext provides routerContext) {
    App()
  }
}
```

### Desktop (`Main.kt`)

On Desktop, the `RouterContext` is created in `Main.kt` using `defaultRouterContext()` and provided to the composable tree using `CompositionLocalProvider`.

```kotlin
val routerContext: RouterContext = defaultRouterContext()
// ...
Window {
  CompositionLocalProvider(LocalRouterContext provides routerContext) {
    App()
  }
}
```

## Screen Navigation

The `Router` is used to navigate between different screens, which are defined as a sealed class called `Screen`. The `navigate` function is used to change the navigation stack.

### `App.kt`

In `App.kt`, the `Router` is created and used to navigate to the initial screen.

```kotlin
val router: Router<Screen> = rememberRouter { listOf(Screen.Home) }
// ...
router.navigate { listOf(Screen.SignIn(model.showOldUserMessage)) }
```

### `TaskChatScreen.kt`, `ScopesScreen.kt`, `SettingsScreen.kt`

In these screens, `RoutedContent` is used to display the content of the current route.

```kotlin
RoutedContent(
  router = router,
  animation = // ...
) { screen ->
  // ...
}
```

## ViewModel Integration

The `koinInjectOnRoute` function suggests that ViewModels are being injected based on the current route. This is done by using the `LocalRouterContext` to get the current `RouterContext` and then using the `key` property of the `RouterContext` to create a unique key for the ViewModel.

### `KoinInjectOnRoute.kt`

```kotlin
@Composable
inline fun <reified T : ViewModel<*, *, *>> koinInjectOnRoute(
  qualifier: Qualifier? = null,
  noinline parameters: ParametersDefinition? = null,
): T {
  val routerContext: RouterContext = LocalRouterContext.current
  return rememberOnRoute(key = routerContext.key) {
    get(qualifier, parameters)
  }
}
```

## Dependency

The library is defined as a dependency in the `gradle/libs.versions.toml` file.

```toml
decompose-router = { module = "io.github.xxfast:decompose-router", version.ref = "decompose-router" }
```
