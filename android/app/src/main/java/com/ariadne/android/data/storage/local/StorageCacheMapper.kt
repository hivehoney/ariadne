package com.ariadne.android.data.storage.local

import com.ariadne.android.data.storage.local.entity.CachedFileEntity
import com.ariadne.android.data.storage.local.entity.CachedStorageEntity
import com.ariadne.android.data.storage.model.StorageConnection
import com.ariadne.android.data.storage.model.StorageFile
import com.ariadne.android.data.storage.model.StorageFileType
import com.ariadne.android.data.storage.model.StorageProviderType
import com.ariadne.android.data.storage.model.StorageSnapshot

/**
 * Room Cache와 공통 Storage 모델 간 변환
 *
 * Room Entity가 상위 Storage 계층에 노출되지 않도록
 * Local DB 모델과 공통 모델 사이의 변환을 담당한다.
 */
object StorageCacheMapper {

    // 공통 Storage 데이터를 Cache Entity로 변환
    fun toStorageEntity( snapshot: StorageSnapshot, syncedAt: Long ): CachedStorageEntity {
        val connection = snapshot.connection

        return CachedStorageEntity(
            providerType = connection.providerType.name,
            accountId = connection.accountId,
            storageName = connection.name,
            account = connection.account,
            availableBytes = connection.availableBytes,
            totalBytes = connection.totalBytes,
            lastSyncedAt = syncedAt
        )
    }

    // 공통 File Metadata를 Cache Entity로 변환
    fun toFileEntities( snapshot: StorageSnapshot ): List<CachedFileEntity> {
        val connection = snapshot.connection

        return snapshot.files.map { file ->
            CachedFileEntity(
                providerType = connection.providerType.name,
                accountId = connection.accountId,
                externalId = file.externalId,
                name = file.name,
                mimeType = file.mimeType,
                modifiedAt = file.modifiedAt,
                size = file.size,
                fileType = file.type.name,
                itemCount = file.itemCount
            )
        }
    }

    // Cache Entity를 공통 Storage 데이터로 변환
    fun toSnapshot(
        storage: CachedStorageEntity,
        files: List<CachedFileEntity>
    ): StorageSnapshot {
        return StorageSnapshot(
            connection = StorageConnection(
                providerType = StorageProviderType.valueOf(storage.providerType),
                accountId = storage.accountId,
                name = storage.storageName,
                account = storage.account,
                availableBytes = storage.availableBytes,
                totalBytes = storage.totalBytes
            ),
            files = files.map(::toStorageFile)
        )
    }

    // Cache File을 공통 File Metadata로 변환
    private fun toStorageFile( file: CachedFileEntity ): StorageFile {
        return StorageFile(
            externalId = file.externalId,
            name = file.name,
            mimeType = file.mimeType,
            modifiedAt = file.modifiedAt,
            size = file.size,
            type = StorageFileType.valueOf(file.fileType),
            itemCount = file.itemCount
        )
    }
}