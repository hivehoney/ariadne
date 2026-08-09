package com.ariadne.backend.storage.domain

import jakarta.persistence.*
import java.time.Instant

/**
 * Ariadne에 연결된 외부 저장소를 나타낸다.
 *
 * Google Drive, OneDrive, Windows 등의 저장소 연결 단위이며,
 * Metadata Sync 시 어떤 StorageProvider를 사용할지 결정하는 기준이 된다.
 */
@Entity
@Table(name = "storage_sources")
open class StorageSource protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    // 저장 유형 (예: 구글, Window, Android)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    open lateinit var type: StorageSourceType
        protected set

    // 표시될 저장소 이름
    @Column(nullable = false,)
    open lateinit var displayName: String
        protected set

    @Column(nullable = false, updatable = false)
    open var createdAt: Instant? = Instant.now()
        protected set

    @Column
    open var lastSyncedAt: Instant? = null
        protected set

    constructor(type: StorageSourceType, displayName: String) : this() {
        require(displayName.isNotBlank()) { "displayName cannot be blank" }

        this.type = type
        this.displayName = displayName
    }

    /**
     * 동기화 시각 기록.
     */
    fun markSynced(syncedAt: Instant) {
        lastSyncedAt = syncedAt
    }
}