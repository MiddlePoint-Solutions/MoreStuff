import SwiftUI
import Lottie
import ComposeApp

@main
struct SwiftUIApp: App {
    @UIApplicationDelegateAdaptor var delegate: AppDelegate
    @Environment(\.scenePhase)  var scenePhase: ScenePhase
    
    init() {
        StartSdkKt.startSdk(navigationHelper: navigationHelper)
        
    }
    
    var defaultRouterContext: RouterContext { delegate.holder.defaultRouterContext }
    var navigationHelper: NavigationHelper { delegate.navigationHelper }
    
    var body: some Scene {
        WindowGroup {
            HomeView(routerContext: defaultRouterContext)
                .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard)
                .onOpenURL { url in
                    
                    handleFileURL(url, navigationHelper: navigationHelper)
                }
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

private func handleFileURL(_ url: URL, navigationHelper: NavigationHelper) {
    let filePath: String?
    
    if url.scheme?.localizedCaseInsensitiveCompare("morestuff") == .orderedSame {
        if #available(iOS 16.0, *) {
            filePath = url.query(percentEncoded: false)
        } else {
            filePath = url.query?.removingPercentEncoding
        }
    } else if url.scheme?.localizedCaseInsensitiveCompare("file") == .orderedSame {
        filePath = url.path
    } else {
        return
    }

    guard let filePath = filePath else {
        return
    }
    
    let fileExtension = (filePath as NSString).pathExtension.lowercased()

    switch fileExtension {
    case "jpg", "jpeg", "png":
        navigationHelper.shareImage(uri: filePath)
    case "pdf":
        navigationHelper.sharePdf(uri: filePath)
    case "txt", "md", "rtf":
        if let content = try? String(contentsOfFile: filePath) {
            navigationHelper.shareText(message: content)
        } else {
            print("Failed to read text file content.")
        }
    default:
        print("Unsupported file type: \(fileExtension)")
    }
}


class DefaultRouterHolder : ObservableObject {
  let defaultRouterContext: RouterContext = DefaultRouterContextKt.defaultRouterContext()

  deinit {
    // Destroy the root component before it is deallocated
    defaultRouterContext.destroy()
  }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    let holder: DefaultRouterHolder = DefaultRouterHolder()
    let navigationHelper: NavigationHelper = NavigationHelper()
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        LottieConfiguration.shared.renderingEngine = .mainThread
        
        let center = UNUserNotificationCenter.current()
        center.delegate = self
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Error al solicitar permisos de notificación: \(error.localizedDescription)")
            } else if granted {
                print("Permisos de notificación concedidos.")
            } else {
                print("Permisos de notificación denegados.")
            }
        }
        
        return true
    }


       /// Manejar notificaciones cuando el usuario las toca
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        print("🔔 didReceiveNotificationResponse ejecutado con userInfo: \(userInfo)")

        if let taskIdString = userInfo["taskId"] as? String, let taskId = Int(taskIdString) {
            print("✅ Notification tapped, navegando a TaskChat(taskId=\(taskId))")
            navigateToTaskChat(taskId: taskId)
            //cancelSchedule(taskId: taskId)
        } else {
            print("⚠️ No taskId encontrado en userInfo")
        }
        if let scheduleId = userInfo["scheduleId"] as? String {
            print("📩 Cancelando scheduleId=\(scheduleId) porque la notificación ha sido recibida")
            center.removePendingNotificationRequests(withIdentifiers: ["SCHEDULE_\(scheduleId)"])
        } else {
            print("⚠️ No scheduleId encontrado en userInfo")
        }
        

        completionHandler()
    }
    
    /// Manejar notificaciones cuando se reciben (incluso en primer plano)
       func userNotificationCenter(_ center: UNUserNotificationCenter,
                                   willPresent notification: UNNotification,
                                   withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
           let userInfo = notification.request.content.userInfo
           print("📩 Notificación recibida en primer plano con userInfo: \(userInfo)")

           if let scheduleId = userInfo["scheduleId"] as? String {
               print("📩 Cancelando scheduleId=\(scheduleId) porque la notificación ha sido recibida")
               center.removePendingNotificationRequests(withIdentifiers: ["SCHEDULE_\(scheduleId)"])
           } else {
               print("⚠️ No scheduleId encontrado en userInfo")
           }
           if let taskIdString = userInfo["taskId"] as? String, let taskId = Int(taskIdString) {
               print("✅ Notification tapped, cancelando a TaskChat(taskId=\(taskId))")
        
               cancelSchedule(taskId: taskId)
           } else {
               print("⚠️ No taskId encontrado en userInfo")
           }

           completionHandler([.banner, .sound])
       }
    
    private func navigateToTaskChat(taskId: Int) {
            DispatchQueue.main.async {
                self.navigationHelper.navigateToTaskChat(taskId: Int64(taskId))
            }
        }
    
    private func cancelSchedule(taskId: Int){
        DispatchQueue.main.async {
            self.navigationHelper.cancelTaskSchedule(taskId: Int64(taskId))
        }
    }

    
   

}



struct HomeView: UIViewControllerRepresentable {
  let routerContext: RouterContext

  func makeUIViewController(context: Context) -> UIViewController {
    return MainViewControllerKt.MainViewController(routerContext: routerContext)
  }

  func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
