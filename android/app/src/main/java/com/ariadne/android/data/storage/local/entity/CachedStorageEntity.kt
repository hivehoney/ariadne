package com.ariadne.android.data.storage.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity

/**
 * 연결된 Storage의 로컬 Cache 정보
 *
 * Provider와 계정 조합으로 Storage를 식별하고,
 * 계정 정보와 용량 및 마지막 Remote 갱신 시점을 저장한다.
 */
@Entity(
    tableName = "cached_storages",
    primaryKeys = ["provider_type", "account_id"]
)
data class CachedStorageEntity(
    @ColumnInfo(name = "provider_type")
    val providerType: String,

    @ColumnInfo(name = "account_id")
    val accountId: String,

    @ColumnInfo(name = "storage_name")
    val storageName: String,

    @ColumnInfo(name = "account")
    val account: String,

    @ColumnInfo(name = "available_bytes")
    val availableBytes: Long?,

    @ColumnInfo(name = "total_bytes")
    val totalBytes: Long?,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long
)