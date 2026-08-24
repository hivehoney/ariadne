package com.ariadne.android.data.storage.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity

/**
 * 외부 Storage 파일의 로컬 Metadata Cache
 *
 * Storage 계정과 Provider 파일 ID를 기준으로 파일을 식별하고,
 * 실제 파일 내용이 아닌 화면 조회에 필요한 Metadata만 저장한다.
 */
@Entity(
    tableName = "cached_files",
    primaryKeys = ["provider_type", "account_id", "external_id"]
)
data class CachedFileEntity(
    @ColumnInfo(name = "provider_type")
    val providerType: String,

    @ColumnInfo(name = "account_id")
    val accountId: String,

    @ColumnInfo(name = "external_id")
    val externalId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "mime_type")
    val mimeType: String,

    @ColumnInfo(name = "modified_at")
    val modifiedAt: String?,

    @ColumnInfo(name = "size")
    val size: Long?,

    @ColumnInfo(name = "file_type")
    val fileType: String,

    @ColumnInfo(name = "item_count")
    val itemCount: Int?
)