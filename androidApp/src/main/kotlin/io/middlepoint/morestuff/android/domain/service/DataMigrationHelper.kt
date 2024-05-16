package io.middlepoint.morestuff.android.domain.service

import android.net.Uri

interface DataMigrationHelper {

    suspend fun exportDatabase(uri: Uri) : Boolean

    suspend fun importDatabase(uri: Uri) : Boolean

}