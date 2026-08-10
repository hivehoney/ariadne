package com.ariadne.backend.storage.domain

import jakarta.persistence.*
import java.time.Instant

/**
 * File이 실제 외부 Storage에 존재하는 위치를 나타낸다.
 *
 * StorageSource와 외부 파일 식별자(externalId)를 통해
 * Provider상의 실제 파일 위치를 식별한다.
 */
@Entity
@Table(name = "file_locations", uniqueConstraints = [
    UniqueConstraint(name = "uk_file_locations_storage_source_external_id" ,
        columnNames = ["storage_source_id", "external_id"]
    )])
open class FileLocation protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    // Ariadne가 관리하는 논리 파일
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    open lateinit var file: File
        protected set

    // 파일이 실제 존재하는 Storage
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "storage_source_id", nullable = false)
    open lateinit var storageSource: StorageSource
        protected set

    // 외부 Storage Provider가 사용하는 파일 식별자
    @Column(name = "external_id", nullable = false, length = 255)
    open lateinit var externalId: String
        protected set

    // 외부 Storage상의 파일 위치
    @Column(length = 2048)
    open var path: String? = null
        protected set

    // 외부 Storage에서 해당 파일이 마지막으로 수정된 시각
    @Column(name = "modified_at")
    open var modifiedAt: Instant? = null
        protected set

    constructor(
        file: File,
        storageSource: StorageSource,
        externalId: String,
        path: String?,
        modifiedAt: Instant?,
    ) : this() {
        require(externalId.isNotBlank()) {
            "externalId cannot be blank"
        }

        require(path == null || path.isNotBlank()) {
            "path cannot be blank"
        }

        this.file = file
        this.storageSource = storageSource
        this.externalId = externalId
        this.path = path
        this.modifiedAt = modifiedAt
    }

    /**
     * 외부 Storage에서 다시 조회된 위치 Metadata를 반영한다.
     */
    fun updateMetadata(
        path: String?,
        modifiedAt: Instant?,
    ) {
        require(path == null || path.isNotBlank()) {
            "path cannot be blank"
        }

        this.path = path
        this.modifiedAt = modifiedAt
    }
}