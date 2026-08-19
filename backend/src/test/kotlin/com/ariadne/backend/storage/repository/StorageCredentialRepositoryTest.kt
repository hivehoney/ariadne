package com.ariadne.backend.storage.repository

import com.ariadne.backend.storage.domain.StorageCredential
import com.ariadne.backend.storage.domain.StorageCredentialStatus
import com.ariadne.backend.storage.domain.StorageCredentialType
import com.ariadne.backend.storage.domain.StorageSource
import com.ariadne.backend.storage.domain.StorageSourceType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@Transactional
class StorageCredentialRepositoryTest(
    @Autowired
    private val storageSourceRepository: StorageSourceRepository,

    @Autowired
    private val storageCredentialRepository: StorageCredentialRepository,
) {

    @Test
    fun `StorageSource의 Credential을 저장하고 조회한다`() {
        // given
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Google Drive",
            ),
        )

        val expiresAt = LocalDateTime.now().plusHours(1)

        val credential = StorageCredential(
            storageSource = storageSource,
            credentialType = StorageCredentialType.OAUTH2,
            credentialStatus = StorageCredentialStatus.ACTIVE,
            externalAccountId = "google-user-123",
            scope = "drive.metadata.readonly",
            credentialData = "encrypted-credential-data",
            credentialSchemaVersion = 1,
            expiresAt = expiresAt,
        )

        storageCredentialRepository.saveAndFlush(credential)

        // when
        val savedCredential =
            storageCredentialRepository.findByStorageSource_Id(
                storageSource.id!!,
            )

        // then
        assertThat(savedCredential).isNotNull

        savedCredential!!

        assertThat(savedCredential.id).isNotNull
        assertThat(savedCredential.storageSource.id)
            .isEqualTo(storageSource.id)

        assertThat(savedCredential.credentialType)
            .isEqualTo(StorageCredentialType.OAUTH2)

        assertThat(savedCredential.credentialStatus)
            .isEqualTo(StorageCredentialStatus.ACTIVE)

        assertThat(savedCredential.externalAccountId)
            .isEqualTo("google-user-123")

        assertThat(savedCredential.scope)
            .isEqualTo("drive.metadata.readonly")

        assertThat(savedCredential.credentialData)
            .isEqualTo("encrypted-credential-data")

        assertThat(savedCredential.credentialSchemaVersion)
            .isEqualTo(1)

        assertThat(savedCredential.expiresAt)
            .isNotNull
    }

    @Test
    fun `하나의 StorageSource에는 Credential을 하나만 저장할 수 있다`() {
        // given
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Google Drive",
            ),
        )

        storageCredentialRepository.saveAndFlush(
            StorageCredential(
                storageSource = storageSource,
                credentialType = StorageCredentialType.OAUTH2,
                credentialData = "credential-1",
            ),
        )

        // when & then
        assertThatThrownBy {
            storageCredentialRepository.saveAndFlush(
                StorageCredential(
                    storageSource = storageSource,
                    credentialType = StorageCredentialType.OAUTH2,
                    credentialData = "credential-2",
                ),
            )
        }.isInstanceOf(DataIntegrityViolationException::class.java)
    }

    @Test
    fun `Credential을 갱신하면 기존 Row를 유지하면서 인증정보가 변경된다`() {
        // given
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Google Drive",
            ),
        )

        val credential = storageCredentialRepository.saveAndFlush(
            StorageCredential(
                storageSource = storageSource,
                credentialType = StorageCredentialType.OAUTH2,
                scope = "drive.metadata.readonly",
                credentialData = "credential-before",
            ),
        )

        val credentialId = credential.id!!
        val refreshedAt = LocalDateTime.now()

        // when
        credential.updateCredential(
            credentialData = "credential-after",
            scope = "drive.metadata.readonly drive.readonly",
            refreshedAt = refreshedAt,
        )

        storageCredentialRepository.flush()

        // then
        val updatedCredential =
            storageCredentialRepository.findById(credentialId)
                .orElseThrow()

        assertThat(updatedCredential.id)
            .isEqualTo(credentialId)

        assertThat(updatedCredential.credentialData)
            .isEqualTo("credential-after")

        assertThat(updatedCredential.scope)
            .isEqualTo("drive.metadata.readonly drive.readonly")

        assertThat(updatedCredential.lastRefreshedAt)
            .isEqualTo(refreshedAt)

        assertThat(updatedCredential.updatedAt)
            .isEqualTo(refreshedAt)
    }

    @Test
    fun `Credential 상태를 변경하면 기존 Row의 상태가 변경된다`() {
        // given
        val storageSource = storageSourceRepository.save(
            StorageSource(
                type = StorageSourceType.GOOGLE_DRIVE,
                displayName = "Google Drive",
            ),
        )

        val credential = storageCredentialRepository.saveAndFlush(
            StorageCredential(
                storageSource = storageSource,
                credentialType = StorageCredentialType.OAUTH2,
                credentialStatus = StorageCredentialStatus.ACTIVE,
                credentialData = "credential",
            ),
        )

        val credentialId = credential.id!!

        // when
        credential.changeStatus(
            StorageCredentialStatus.REVOKED,
        )

        storageCredentialRepository.flush()

        // then
        val updatedCredential =
            storageCredentialRepository.findById(credentialId)
                .orElseThrow()

        assertThat(updatedCredential.id)
            .isEqualTo(credentialId)

        assertThat(updatedCredential.credentialStatus)
            .isEqualTo(StorageCredentialStatus.REVOKED)
    }
}