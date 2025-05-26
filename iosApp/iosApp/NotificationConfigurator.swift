//
//  NotificationConfigurator.swift
//  iosApp
//
//  Created by Àlex Gallardo Moreno on 26/5/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import UserNotifications
import UIKit

class NotificationConfigurator{
    func configureNotifications() {
        let center = UNUserNotificationCenter.current()
        
        
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
    }
}
