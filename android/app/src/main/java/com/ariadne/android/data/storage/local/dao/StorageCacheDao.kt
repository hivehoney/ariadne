package com.ariadne.android.data.storage.local.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.ariadne.android.data.storage.local.entity.CachedFileEntity
import com.ariadne.android.data.storage.local.entity.CachedStorageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Storage Metadata Cache 접근을 담당하는 DAO
 *
 * Storage와 File Cache 조회 및 갱신을 제공하고,
 * Remote Snapshot 반영을 하나의 Transaction으로 처리한다.
 */
@Dao
abstract class StorageCacheDao {

    // Provider의 가장 최근 Storage Cache 관찰
    @Query(
        """
        SELECT *
        FROM cached_storages
        WHERE provider_type = :providerType
        ORDER BY last_synced_at DESC
        LIMIT 1
        """
    )
    abstract fun observeLatestStorage(
        providerType: String
    ): Flow<CachedStorageEntity?>

    // 특정 Storage 계정의 File Cache 관찰
    @Query(
        """
        SELECT *
        FROM cached_files
        WHERE provider_type = :providerType
          AND account_id = :accountId
        ORDER BY
            CASE WHEN file_type = 'FOLDER' THEN 0 ELSE 1 END,
            name COLLATE NOCASE ASC
        """
    )
    abstract fun observeFiles(
        providerType: String,
        accountId: String
    ): Flow<List<CachedFileEntity>>

    // Storage Cache 생성 또는 갱신
    @Upsert
    protected abstract suspend fun upsertStorage(
        storage: CachedStorageEntity
    )

    // File Cache 생성 또는 갱신
    @Upsert
    protected abstract suspend fun upsertFiles(
        files: List<CachedFileEntity>
    )

    // 특정 Storage의 기존 File Cache 제거
    @Query(
        """
        DELETE FROM cached_files
        WHERE provider_type = :providerType
          AND account_id = :accountId
        """
    )
    protected abstract suspend fun deleteFiles(
        providerType: String,
        accountId: String
    )

    // Storage Cache 제거
    @Query(
        """
        DELETE FROM cached_storages
        WHERE provider_type = :providerType
          AND account_id = :accountId
        """
    )
    protected abstract suspend fun deleteStorage(
        providerType: String,
        accountId: String
    )

    /**
     * Remote에서 조회한 Storage Snapshot으로 Cache 교체
     */
    @Transaction
    open suspend fun replaceSnapshot(
        storage: CachedStorageEntity,
        files: List<CachedFileEntity>
    ) {
        upsertStorage(storage)
        deleteFiles(
            providerType = storage.providerType,
            accountId = storage.accountId
        )

        if (files.isNotEmpty()) {
            upsertFiles(files)
        }
    }

    /**
     * 연결 해제된 Storage의 Local Cache 제거
     */
    @Transaction
    open suspend fun clearStorage(
        providerType: String,
        accountId: String
    ) {
        deleteFiles(
            providerType = providerType,
            accountId = accountId
        )
        deleteStorage(
            providerType = providerType,
            accountId = accountId
        )
    }

    // Provider의 가장 최근 Storage Cache 조회
    @Query(
        """
        SELECT *
        FROM cached_storages
        WHERE provider_type = :providerType
        ORDER BY last_synced_at DESC
        LIMIT 1
        """
    )
    abstract suspend fun getLatestStorage(
        providerType: String
    ): CachedStorageEntity?
}