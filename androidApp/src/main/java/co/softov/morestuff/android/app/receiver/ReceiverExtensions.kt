package co.softov.morestuff.android.app.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.KEY_REPLY_EXTRA
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.KEY_REVIEW_EXTRA
import co.softov.morestuff.android.app.receiver.NotificationReceiver.Companion.KEY_SCHEDULE_ID

import co.softov.morestuff.android.domain.enums.ReplyType
import co.softov.morestuff.android.domain.enums.ReviewNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random


val random = Random(3112)
val randomRequestCode: Int
    get() = ((System.currentTimeMillis() + random.nextInt()) / 1000).toInt()

fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob()).launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
        }
    }
}

fun NotificationReceiver.Companion.createReplyIntent(
    context: Context,
    scheduleId: Long,
    type: ReplyType
): PendingIntent =
    Intent(context, NotificationReceiver::class.java)
        .setAction(ACTION_NOTIFICATION_REPLY)
        .setReplayIntentExtras(scheduleId, type)
        .let {
            val requestCode = randomRequestCode
            Timber.d("createReplyIntent, requestCode: $requestCode")
            // Use random request code for replying to reminder intent.
            PendingIntent.getBroadcast(context, requestCode, it, PendingIntent.FLAG_IMMUTABLE)
        }

fun NotificationReceiver.Companion.createReviewIntent(
    context: Context,
    type: ReviewNotification
) = Intent(context, NotificationReceiver::class.java)
    .setAction(ACTION_NOTIFICATION_REVIEW)
    .setReviewIntentExtras(type)

fun NotificationReceiver.Companion.createReviewPendingIntent(
    context: Context,
    intent: Intent,
    type: ReviewNotification
) = PendingIntent.getBroadcast(
    context,
    getReviewIntentRequestCode(type),
    intent,
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)


fun NotificationReceiver.Companion.createCancelReviewPendingIntent(
    context: Context,
    intent: Intent,
    type: ReviewNotification
) = PendingIntent.getBroadcast(
    context,
    getReviewIntentRequestCode(type),
    intent,
    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
)


fun NotificationReceiver.Companion.getReviewIntentRequestCode(
    type: ReviewNotification
) = when (type) {
    ReviewNotification.Morning -> REQUEST_CODE_REVIEW_MORNING
    ReviewNotification.Afternoon -> REQUEST_CODE_REVIEW_AFTERNOON
    ReviewNotification.Evening -> REQUEST_CODE_REVIEW_EVENING
}

fun Intent.getScheduleIdExtra() = getLongExtra(KEY_SCHEDULE_ID, 0)
fun Intent.setScheduleIdExtra(scheduleId: Long) = putExtra(KEY_SCHEDULE_ID, scheduleId)

fun Intent.setReplayIntentExtras(scheduleId: Long, type: ReplyType) =
    putExtra(KEY_REPLY_EXTRA, ReplyIntentExtras(scheduleId, type))

fun Intent.getReplyIntentExtras(): ReplyIntentExtras? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(KEY_REPLY_EXTRA, ReplyIntentExtras::class.java)
    } else getParcelableExtra(KEY_REPLY_EXTRA)

fun Intent.setReviewIntentExtras(type: ReviewNotification?) =
    putExtra(KEY_REVIEW_EXTRA, type as? Parcelable)

fun Intent.getReviewIntentExtras(): ReviewNotification? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(KEY_REVIEW_EXTRA, ReviewNotification::class.java)
    } else getParcelableExtra(KEY_REPLY_EXTRA)

@Parcelize
data class ReplyIntentExtras(
    val scheduleId: Long,
    val type: ReplyType
) : Parcelable