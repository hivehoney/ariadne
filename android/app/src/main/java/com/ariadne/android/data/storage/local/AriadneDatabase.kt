package com.ariadne.android.data.storage.local

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.ariadne.android.data.storage.local.dao.StorageCacheDao
import com.ariadne.android.data.storage.local.entity.CachedFileEntity
import com.ariadne.android.data.storage.local.entity.CachedStorageEntity

/**
 * Ariadne Android Local Database
 *
 * 외부 Storage의 연결 정보와 File Metadata Cache를 관리한다.
 * 실제 파일 내용이나 인증 Token은 저장하지 않는다.
 */
@Database(
    entities = [
        CachedStorageEntity::class,
        CachedFileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AriadneDatabase : RoomDatabase() {

    // Storage Metadata Cache DAO 제공
    abstract fun storageCacheDao(): StorageCacheDao
}