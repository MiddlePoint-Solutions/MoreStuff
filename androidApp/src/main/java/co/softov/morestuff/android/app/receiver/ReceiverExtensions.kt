package co.softov.morestuff.android.app.receiver

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable

import co.softov.morestuff.android.domain.enums.ReplyType
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import kotlin.random.Random

const val KEY_SCHEDULE_ID = "SCHEDULE_ID"
const val KEY_REPLY_EXTRA = "REPLY_EXTRA"
const val KEY_TASK_ID = "TASK_ID"

val random = Random(3112)

val randomRequestCode: Int
    get() = ((System.currentTimeMillis() + random.nextInt()) / 1000).toInt()

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

fun Intent.getScheduleIdExtra() = getLongExtra(KEY_SCHEDULE_ID, 0)
fun Intent.setScheduleIdExtra(scheduleId: Long) = putExtra(KEY_SCHEDULE_ID, scheduleId)

fun Intent.setReplayIntentExtras(scheduleId: Long, type: ReplyType) =
    putExtra(KEY_REPLY_EXTRA, ReplyIntentExtras(scheduleId, type))

fun Intent.getReplyIntentExtras(): ReplyIntentExtras? =
    getParcelableExtra(KEY_REPLY_EXTRA)

@Parcelize
data class ReplyIntentExtras(
    val scheduleId: Long,
    val type: ReplyType
) : Parcelable