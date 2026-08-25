package com.ariadne.android.data.storage.local

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver

/**
 * Ariadne Local Database 인스턴스 관리
 *
 * Application 전체에서 하나의 Room Database를 재사용하고,
 * Storage Metadata Cache DAO 접근 기반을 제공한다.
 */
object AriadneDatabaseProvider {

    // 현재 Database 인스턴스
    @Volatile
    private var instance: AriadneDatabase? = null

    // Ariadne Local Database 제공
    fun getDatabase( context: Context ): AriadneDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AriadneDatabase::class.java,
                DATABASE_NAME
            )
                .setDriver(AndroidSQLiteDriver())
                .build()
                .also { database ->
                    instance = database
                }
        }
    }

    private const val DATABASE_NAME = "ariadne.db"
}