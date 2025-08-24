package io.middlepoint.morestuff.shared.app.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import co.touchlab.kermit.Logger
import io.middlepoint.morestuff.shared.app.extensions.getParcelableExtraCompat
import io.middlepoint.morestuff.shared.app.receiver.NotificationReceiver.Companion.KEY_REPLY_EXTRA
import io.middlepoint.morestuff.shared.domain.enums.ReplyType
import io.middlepoint.morestuff.shared.domain.model.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
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
  scheduleId: Uuid,
  type: ReplyType
): PendingIntent =
  Intent(context, NotificationReceiver::class.java)
    .setAction(ACTION_NOTIFICATION_REPLY)
    .setReplayIntentExtras(scheduleId, type)
    .let {
      val requestCode = randomRequestCode
      Logger.d("createReplyIntent, requestCode: $requestCode")
      // Use random request code for replying to reminder intent.
      PendingIntent.getBroadcast(context, requestCode, it, PendingIntent.FLAG_IMMUTABLE)
    }

fun NotificationReceiver.Companion.createReviewIntent(
  context: Context,
) = Intent(context, NotificationReceiver::class.java)
  .setAction(ACTION_NOTIFICATION_REVIEW)

fun NotificationReceiver.Companion.createReviewPendingIntent(
  context: Context,
  intent: Intent,
): PendingIntent = PendingIntent.getBroadcast(
  context,
  REQUEST_CODE_REVIEW,
  intent,
  PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)


fun NotificationReceiver.Companion.createCancelReviewPendingIntent(
  context: Context,
  intent: Intent,
): PendingIntent? = PendingIntent.getBroadcast(
  context,
  REQUEST_CODE_REVIEW,
  intent,
  PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
)

fun Intent.setReplayIntentExtras(scheduleId: Uuid, type: ReplyType) =
  putExtra(KEY_REPLY_EXTRA, ReplyIntentExtras(scheduleId.value, type))

fun Intent.getReplyIntentExtras(): ReplyIntentExtras? =
  getParcelableExtraCompat(KEY_REPLY_EXTRA, ReplyIntentExtras::class.java)

@Parcelize
data class ReplyIntentExtras(
  val scheduleId: String,
  val type: ReplyType
) : Parcelable