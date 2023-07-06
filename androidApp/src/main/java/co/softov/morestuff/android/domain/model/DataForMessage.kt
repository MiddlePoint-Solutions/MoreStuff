package co.softov.morestuff.android.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class DataForMessage(
    val id: Long,
    val fileName: String,
    val filePath: String,
    val creationTime: String,
)