import SwiftUI
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
                    print("url type: \(url)")
                    
                    if url.absoluteString.contains("app.morestuff://login-callback") && url.absoluteString.contains("access_token=") {
                        if let fragment = url.fragment {
                            print("Fragment: \(fragment)")
                            delegate.navigateHome(accessToken: fragment)
                        } else {
                            print("No fragment found in URL")
                        }
                    } else {
                        handleFileURL(url, navigationHelper: navigationHelper)
                    }
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
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        let center = UNUserNotificationCenter.current()
        center.delegate = self

    
        let snoozeAction = UNNotificationAction(
            identifier: "SNOOZE_ACTION",
            title: "Snooze",
            options: []
        )

        let tomorrowAction = UNNotificationAction(
            identifier: "TOMORROW_ACTION",
            title: "Tomorrow",
            options: []
        )

        let doneAction = UNNotificationAction(
            identifier: "DONE_ACTION",
            title: "Done",
            options: [.authenticationRequired]
        )

        
        let taskCategory = UNNotificationCategory(
            identifier: "TASK_REPLY_CATEGORY",
            actions: [snoozeAction, tomorrowAction, doneAction],
            intentIdentifiers: [],
            options: [.customDismissAction]
        )

        
        center.setNotificationCategories([taskCategory])

       
        center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Error al solicitar permisos de notificación: \(error.localizedDescription)")
            } else if granted {
                print("Permisos de notificación concedidos.")
            } else {
                print("Permisos de notificación denegados.")
            }
        }

        navigationHelper.triggerDataSyncSchedule()
        return true
    }



    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo

        if let scheduleId = userInfo["scheduleId"] as? String {
            switch response.actionIdentifier {
            case "SNOOZE_ACTION":
                print("🔁 Snooze pressed for schedule \(scheduleId)")
                self.replyToSchedule(scheduleId: scheduleId, replyType: "SNOOZE")

            case "TOMORROW_ACTION":
                print("📅 Tomorrow pressed for schedule \(scheduleId)")
                self.replyToSchedule(scheduleId: scheduleId, replyType: "TOMORROW")

            case "DONE_ACTION":
                print("✅ Done pressed for schedule \(scheduleId)")
                self.replyToSchedule(scheduleId: scheduleId, replyType: "DONE")
                
            case UNNotificationDismissActionIdentifier:
                print("👋 Swipe to dismiss para schedule \(scheduleId)")
                if let taskIdString = userInfo["taskId"] as? String {
                    cancelSchedule(taskId: taskIdString)
                }


            default:
                if let taskIdString = userInfo["taskId"] as? String {
                    navigateToTaskChat(taskId: taskIdString)
                }
            }

            center.removePendingNotificationRequests(withIdentifiers: ["SCHEDULE_\(scheduleId)"])
        }

        completionHandler()
    }

    
       func userNotificationCenter(_ center: UNUserNotificationCenter,
                                   willPresent notification: UNNotification,
                                   withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
           let userInfo = notification.request.content.userInfo

           if let scheduleId = userInfo["scheduleId"] as? String {
               center.removePendingNotificationRequests(withIdentifiers: ["SCHEDULE_\(scheduleId)"])
           }
           if let taskIdString = userInfo["taskId"] as? String {
               cancelSchedule(taskId: taskIdString)
           }

           completionHandler([.banner, .sound])
       }
    
    private func navigateToTaskChat(taskId: String) {
        DispatchQueue.main.async {
            self.navigationHelper.navigateToTaskChat(taskId: taskId)
        }
    }

    
    private func cancelSchedule(taskId: String){
        DispatchQueue.main.async {
            self.navigationHelper.cancelTaskSchedule(taskId: taskId)
        }
    }
    
    func navigateHome(accessToken: String? = nil) {
        DispatchQueue.main.async {
            print("token de acceso: \(accessToken)")
            self.navigationHelper.navigateToHome(accessToken: accessToken)
        }
    }
    
    private func replyToSchedule(scheduleId: String, replyType: String) {
        DispatchQueue.main.async {
            self.navigationHelper.replyToSchedule(
                scheduleId: scheduleId,
                replyTypeString: replyType
            )
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
