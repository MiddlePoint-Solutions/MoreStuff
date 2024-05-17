package io.middlepoint.morestuff.shared.domain.service

import android.net.Uri

interface DataMigrationHelper {

    suspend fun exportDatabase(uri: Uri) : Boolean

    suspend fun importDatabase(uri: Uri) : Boolean

}