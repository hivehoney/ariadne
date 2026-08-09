package com.ariadne.backend.storage.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * OneSearch가 관리하는 논리적인 파일을 나타낸다.
 *
 * 실제 저장 위치는 FileLocation에서 관리하며,
 * File은 Storage 종류와 관계없는 파일 자체의 Metadata를 가진다.
 */
@Entity
@Table(name = "files")
open class File protected constructor() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(nullable = false)
    open lateinit var name: String
        protected set

    // 파일 종류
    @Column(nullable = false, length = 255)
    open lateinit var mimeType: String
        protected set

    // Metadata 기본 정보
    @Column(nullable = false)
    open var size: Long = 0
        protected set

    // Ariadne 내부 생성 시점
    @Column(nullable = false, updatable = false)
    open var createdAt: Instant? = Instant.now()
        protected set

    // Metadata 변경 반영 시점
    @Column(nullable = false)
    open var updatedAt: Instant? = createdAt
        protected set

    constructor(name: String, mimeType: String, size: Long) : this() {
        require(name.isNotBlank()) {
            "name cannot be blank"
        }

        require(mimeType.isNotBlank()) {
            "mimeType cannot be blank"
        }

        require(size >= 0) {
            "size cannot be negative"
        }

        this.name = name
        this.mimeType = mimeType
        this.size = size
    }

    /**
     * 외부 Metadata 변경 내용을 File에 반영한다.
     */
    fun updateMetadata(
        name: String,
        mimeType: String,
        size: Long,
        updatedAt: Instant,
    ) {
        require(name.isNotBlank()) {
            "name cannot be blank"
        }

        require(mimeType.isNotBlank()) {
            "mimeType cannot be blank"
        }

        require(size >= 0) {
            "size cannot be negative"
        }

        this.name = name
        this.mimeType = mimeType
        this.size = size
        this.updatedAt = updatedAt
    }
}