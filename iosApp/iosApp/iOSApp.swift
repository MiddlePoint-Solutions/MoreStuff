import SwiftUI
import ComposeApp

@main
struct SwiftUIApp: App {
  @UIApplicationDelegateAdaptor var delegate: AppDelegate
  @Environment(\.scenePhase)  var scenePhase: ScenePhase
    
    init() {
        PlatformModuleKt.doInitKoin { Koin_coreKoinApplication in
            
        }
    }

  var defaultRouterContext: RouterContext { delegate.holder.defaultRouterContext }

  var body: some Scene {
    WindowGroup {
      HomeView(routerContext: defaultRouterContext)
            .ignoresSafeArea(.keyboard)
    }
    .onChange(of: scenePhase) { newPhase in
        switch newPhase {
        case .background: defaultRouterContext.stop()
        case .inactive: defaultRouterContext.pause()
        case .active: defaultRouterContext.resume()
        @unknown default: break
        }
    } // Compose has own keyboard handler
  }
}

class DefaultRouterHolder : ObservableObject {
  let defaultRouterContext: RouterContext = DefaultRouterContextKt.defaultRouterContext()

  deinit {
    // Destroy the root component before it is deallocated
    defaultRouterContext.destroy()
  }
}

class AppDelegate: NSObject, UIApplicationDelegate {
    let holder: DefaultRouterHolder = DefaultRouterHolder()
}



struct HomeView: UIViewControllerRepresentable {
  let routerContext: RouterContext

  func makeUIViewController(context: Context) -> UIViewController {
    return MainViewControllerKt.MainViewController(routerContext: routerContext)
  }

  func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
