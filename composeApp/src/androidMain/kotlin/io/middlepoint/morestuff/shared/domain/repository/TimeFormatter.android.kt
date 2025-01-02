package io.middlepoint.morestuff.shared.domain.repository

import android.text.format.DateFormat
import io.middlepoint.morestuff.shared.MoreStuffApp

actual fun is24HourFormat(): Boolean = DateFormat.is24HourFormat(MoreStuffApp.INSTANCE)