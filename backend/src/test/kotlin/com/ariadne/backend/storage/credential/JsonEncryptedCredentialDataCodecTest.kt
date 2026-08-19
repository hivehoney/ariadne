package com.ariadne.backend.storage.credential

import com.ariadne.backend.storage.adapter.GoogleDriveCredentialData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.util.Base64

class JsonEncryptedCredentialDataCodecTest {

    /*
     * 실제 운영 Key가 아니라 테스트에서만 사용하는 고정 AES-256 Key다.
     */
    private val encryptionKey = Base64.getEncoder()
        .encodeToString(ByteArray(32) { (it + 1).toByte() })

    private val credentialCipher = AesGcmCredentialCipher(
        encodedKey = encryptionKey,
    )

    /*
     * Kotlin data class를 정상적으로 역직렬화하기 위해
     * Kotlin module이 등록된 ObjectMapper를 사용한다.
     */
    private val objectMapper = jacksonObjectMapper()

    private val credentialDataCodec = JsonEncryptedCredentialDataCodec(
        objectMapper = objectMapper,
        credentialCipher = credentialCipher,
    )

    @Test
    fun `Google Drive CredentialData를 암호화하여 저장하고 다시 복원한다`() {
        // given
        val credentialData = GoogleDriveCredentialData(
            refreshToken = "google-refresh-token",
        )

        // when
        val encoded = credentialDataCodec.encode(credentialData)
        val decoded = credentialDataCodec.decode(
            encoded,
            GoogleDriveCredentialData::class,
        )

        // then
        assertThat(decoded).isEqualTo(credentialData)
    }

    @Test
    fun `저장되는 CredentialData에는 Refresh Token 평문이 노출되지 않는다`() {
        // given
        val refreshToken = "secret-google-refresh-token"
        val credentialData = GoogleDriveCredentialData(
            refreshToken = refreshToken,
        )

        // when
        val encoded = credentialDataCodec.encode(credentialData)

        // then
        assertThat(encoded).doesNotContain(refreshToken)
        assertThat(encoded).startsWith("v1:")
    }

    @Test
    fun `같은 CredentialData를 저장해도 암호화 결과는 매번 달라진다`() {
        // given
        val credentialData = GoogleDriveCredentialData(
            refreshToken = "google-refresh-token",
        )

        // when
        val first = credentialDataCodec.encode(credentialData)
        val second = credentialDataCodec.encode(credentialData)

        // then
        assertThat(first).isNotEqualTo(second)

        val firstDecoded = credentialDataCodec.decode(
            first,
            GoogleDriveCredentialData::class,
        )

        val secondDecoded = credentialDataCodec.decode(
            second,
            GoogleDriveCredentialData::class,
        )

        assertThat(firstDecoded).isEqualTo(credentialData)
        assertThat(secondDecoded).isEqualTo(credentialData)
    }
}