package com.ariadne.android.data.storage

import com.ariadne.android.data.storage.local.StorageCacheMapper
import com.ariadne.android.data.storage.local.dao.StorageCacheDao
import com.ariadne.android.data.storage.model.StorageProviderType
import com.ariadne.android.data.storage.model.StorageSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Storage Local Cache와 Remote API를 조정하는 Repository
 *
 * Room Cache를 화면 데이터의 기준으로 제공하고,
 * StorageClient에서 조회한 최신 Metadata를 Cache에 반영한다.
 */
class StorageRepository(
    private val client: StorageClient,
    private val cacheDao: StorageCacheDao,
    private val providerType: StorageProviderType
) {

    // 현재 Provider의 Local Cache 관찰
    fun observeSnapshot(): Flow<StorageSnapshot?> {
        return cacheDao.observeLatestStorage(providerType.name)
            .flatMapLatest { storage ->
                if (storage == null) {
                    flowOf(null)
                } else {
                    cacheDao.observeFiles(
                        providerType = storage.providerType,
                        accountId = storage.accountId
                    ).map { files ->
                        StorageCacheMapper.toSnapshot(
                            storage = storage,
                            files = files
                        )
                    }
                }
            }
    }

    // Remote Metadata 조회 후 Local Cache 갱신
    suspend fun refresh() {
        val snapshot = client.loadRoot()

        require(snapshot.connection.providerType == providerType) {
            "Storage Provider가 일치하지 않습니다."
        }

        val syncedAt = System.currentTimeMillis()

        cacheDao.replaceSnapshot(
            storage = StorageCacheMapper.toStorageEntity(
                snapshot = snapshot,
                syncedAt = syncedAt
            ),
            files = StorageCacheMapper.toFileEntities(snapshot)
        )
    }
}