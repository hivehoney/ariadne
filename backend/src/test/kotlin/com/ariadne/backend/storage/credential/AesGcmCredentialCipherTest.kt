package com.ariadne.backend.storage.credential

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import java.util.Base64

class AesGcmCredentialCipherTest {

    private val encryptionKey =
        Base64.getEncoder().encodeToString(
            ByteArray(32) {
                (it + 1).toByte()
            },
        )

    private val credentialCipher =
        AesGcmCredentialCipher(
            encodedKey = encryptionKey,
        )

    @Test
    fun `Credential을 암호화하고 다시 복호화할 수 있다`() {
        // given
        val plainText = """
            {
              "refreshToken": "google-refresh-token"
            }
        """.trimIndent()

        // when
        val encrypted =
            credentialCipher.encrypt(plainText)

        val decrypted =
            credentialCipher.decrypt(encrypted)

        // then
        assertThat(encrypted)
            .isNotEqualTo(plainText)

        assertThat(encrypted)
            .startsWith("v1:")

        assertThat(decrypted)
            .isEqualTo(plainText)
    }

    @Test
    fun `같은 Credential을 암호화해도 결과는 매번 달라진다`() {
        // given
        val plainText = "refresh-token"

        // when
        val first =
            credentialCipher.encrypt(plainText)

        val second =
            credentialCipher.encrypt(plainText)

        // then
        assertThat(first)
            .isNotEqualTo(second)

        assertThat(
            credentialCipher.decrypt(first),
        ).isEqualTo(plainText)

        assertThat(
            credentialCipher.decrypt(second),
        ).isEqualTo(plainText)
    }

    @Test
    fun `잘못된 암호화 Key 길이는 사용할 수 없다`() {
        // given
        val invalidKey =
            Base64.getEncoder()
                .encodeToString(
                    ByteArray(16),
                )

        // when & then
        assertThatThrownBy {
            AesGcmCredentialCipher(
                encodedKey = invalidKey,
            )
        }.isInstanceOf(
            IllegalArgumentException::class.java,
        )
    }

    @Test
    fun `지원하지 않는 암호화 버전은 복호화할 수 없다`() {
        // given
        val encrypted =
            credentialCipher.encrypt(
                "refresh-token",
            )

        val unsupported =
            encrypted.replaceFirst(
                "v1:",
                "v2:",
            )

        // when & then
        assertThatThrownBy {
            credentialCipher.decrypt(
                unsupported,
            )
        }.isInstanceOf(
            IllegalArgumentException::class.java,
        )
    }

    @Test
    fun `암호화된 Credential이 변조되면 복호화할 수 없다`() {
        // given
        val encrypted =
            credentialCipher.encrypt(
                "refresh-token",
            )

        val parts = encrypted.split(
            ":",
            limit = 2,
        )

        val payload = Base64.getDecoder()
            .decode(parts[1])

        payload[payload.lastIndex] =
            (payload.last() + 1).toByte()

        val tampered =
            "v1:${
                Base64.getEncoder()
                    .encodeToString(payload)
            }"

        // when & then
        assertThatThrownBy {
            credentialCipher.decrypt(
                tampered,
            )
        }.isInstanceOf(
            Exception::class.java,
        )
    }
}