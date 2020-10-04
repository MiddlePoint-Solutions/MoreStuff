package co.softov.morestuff.androidApp.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import co.softov.morestuff.android.R
import kotlinx.coroutines.launch
import co.softov.morestuff.androidApp.domain.usecase.schedule.BootCompleteScheduler
import org.koin.android.ext.android.inject

class BootService : BaseService(BootService::class.java.simpleName) {

    private val bootScheduler: BootCompleteScheduler by inject()

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onHandleIntent(intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(3112, createNotification())
        }

        serviceScope.launch {
            bootScheduler()
        }
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        return NotificationCompat.Builder(application, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("MoreStuff")
            .setContentText("Rescheduling reminders")
            .setSmallIcon(R.drawable.ic_chat_24dp)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "ReminderNotifications"
    }

    // TODO: create notification channel should be when app first runs (maybe in applicaion class?)
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Notifications"
            val descriptionText = "Notifications for your reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system

            notificationManager.createNotificationChannel(channel)
        }
    }
}