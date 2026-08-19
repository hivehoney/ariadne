package com.ariadne.backend.storage.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * Ariadne에 연결된 외부 Storage의 인증정보를 관리한다.
 *
 * Google Drive, OneDrive, Windows 등 Provider마다 인증 방식과
 * 실제 Credential 구조는 다를 수 있으므로 Provider별 세부 인증정보는
 * credentialData에 저장한다.
 *
 * Provider와 무관하게 공통으로 관리할 수 있는 인증 방식, 상태,
 * 외부 계정 식별자, 권한 범위, 만료 정보 등은 별도 컬럼으로 관리한다.
 */
@Entity
@Table(name = "storage_credentials")
class StorageCredential(

    /**
     * 이 Credential이 어느 Storage 연결에 속하는지를 나타낸다.
     * 하나의 StorageSource에는 하나의 Credential만 존재한다.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "storage_source_id",
        nullable = false,
        unique = true,
    )
    val storageSource: StorageSource,

    /**
     * 해당 Storage가 사용하는 인증 방식.
     * 예:
     * Google Drive / OneDrive -> OAUTH2
     * Windows Device         -> DEVICE
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "credential_type",
        nullable = false,
        length = 50,
    )
    val credentialType: StorageCredentialType,

    /**
     * 현재 Credential의 사용 가능 상태.
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "credential_status",
        nullable = false,
        length = 50,
    )
    var credentialStatus: StorageCredentialStatus = StorageCredentialStatus.ACTIVE,

    /**
     * 외부 Provider가 제공하는 계정 식별자.
     *
     * email처럼 변경 가능한 값보다 Provider의 고유 사용자 ID를 저장한다.
     * Provider에 해당 개념이 없으면 null일 수 있다.
     */
    @Column(
        name = "external_account_id",
        length = 255,
    )
    val externalAccountId: String? = null,

    /**
     * 현재 Credential에 부여된 권한 범위.
     * OAuth Provider에서 사용하며 DEVICE 방식 등에서는 null일 수 있다.
     */
    @Column(
        name = "scope",
        columnDefinition = "TEXT",
    )
    var scope: String? = null,

    /**
     * Provider별 실제 인증정보.
     *
     * 향후:
     * Provider별 CredentialData -> JSON -> 암호화 -> credentialData
     */
    @Column(
        name = "credential_data",
        nullable = false,
        columnDefinition = "TEXT",
    )
    var credentialData: String,

    /**
     * credentialData 내부 구조의 버전.
     *
     * Credential payload 형식이 변경되더라도 기존 Row를
     * 어떤 형식으로 역직렬화해야 하는지 판단하기 위해 사용한다.
     */
    @Column(
        name = "credential_schema_version",
        nullable = false,
    )
    var credentialSchemaVersion: Int = 1,

    /**
     * 현재 Credential 또는 인증 세션의 만료 시각.
     *
     * 해당 개념이 없는 Provider에서는 null이다.
     */
    @Column(name = "expires_at")
    var expiresAt: LocalDateTime? = null,

    /**
     * Refresh Credential 자체의 만료 시각.
     *
     * Provider가 별도 Refresh Token 만료를 제공하지 않으면 null이다.
     */
    @Column(name = "refresh_expires_at")
    var refreshExpiresAt: LocalDateTime? = null,

    /**
     * 마지막으로 인증정보를 정상 갱신한 시각.
     */
    @Column(name = "last_refreshed_at")
    var lastRefreshedAt: LocalDateTime? = null,

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false,
    )
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(
        name = "updated_at",
        nullable = false,
    )
    var updatedAt: LocalDateTime = createdAt,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    /**
     * Provider 인증정보가 갱신됐을 때 Credential을 변경한다.
     *
     * Entity가 Managed 상태라면 별도 save() 없이 Dirty Checking으로 반영된다.
     */
    fun updateCredential(
        credentialData: String,
        credentialSchemaVersion: Int = this.credentialSchemaVersion,
        scope: String? = this.scope,
        expiresAt: LocalDateTime? = this.expiresAt,
        refreshExpiresAt: LocalDateTime? = this.refreshExpiresAt,
        refreshedAt: LocalDateTime = LocalDateTime.now(),
    ) {
        this.credentialData = credentialData
        this.credentialSchemaVersion = credentialSchemaVersion
        this.scope = scope
        this.expiresAt = expiresAt
        this.refreshExpiresAt = refreshExpiresAt
        this.lastRefreshedAt = refreshedAt
        this.updatedAt = refreshedAt
    }

    fun changeStatus(
        status: StorageCredentialStatus,
        updatedAt: LocalDateTime = LocalDateTime.now(),
    ) {
        this.credentialStatus = status
        this.updatedAt = updatedAt
    }

    /**
     * Refresh Token을 사용해 Access Token을 정상적으로 재발급한 시점을 기록한다.
     */
    fun markRefreshed(refreshedAt: LocalDateTime = LocalDateTime.now()) {
        lastRefreshedAt = refreshedAt
        updatedAt = refreshedAt
    }
}