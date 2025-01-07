package io.middlepoint.morestuff.shared.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.key
import io.github.xxfast.decompose.router.rememberOnRoute
import org.koin.compose.LocalKoinApplication
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import kotlin.reflect.KClass

@Composable
fun <T : Any> koinInjectOnRoute(
  type: KClass<T>,
  key: Any = type.key,
  parameters: ParametersDefinition? = null,
  block: @DisallowComposableCalls ((koin: Koin) -> T)? = null
): T {
  class RouteInstance(val instance: T) : InstanceKeeper.Instance

  val routerContext: RouterContext = LocalRouterContext.current
  val koin: Koin = LocalKoinApplication.current
  val instanceKeeper: InstanceKeeper = routerContext.instanceKeeper
  val routeInstance: RouteInstance = remember(key) {
    instanceKeeper.getOrCreate(key) {
      RouteInstance(
        block?.invoke(koin) ?: koin.get(clazz = type, parameters = parameters)
      )
    }
  }
  rememberOnRoute { }
  return routeInstance.instance
}

@Suppress("DEPRECATION")
@Composable
inline fun <reified T : Any> koinInjectOnRoute(
  key: Any = T::class,
  noinline parameters: ParametersDefinition? = null,
): T {
  val koin: Koin = LocalKoinApplication.current
  return rememberOnRoute(T::class, key) { koin.get(clazz = T::class, parameters = parameters) }
}
