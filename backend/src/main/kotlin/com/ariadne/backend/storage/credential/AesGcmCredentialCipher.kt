package com.ariadne.backend.storage.credential

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES-GCM 기반 Credential 암호화 구현체.
 *
 * Refresh Token과 같은 Credential은 외부 Provider API 호출 시
 * 원문을 다시 사용해야 하므로 단방향 Hash가 아닌 복호화 가능한 암호화가 필요하다.
 *
 * AES-GCM은 암호화와 함께 Authentication Tag를 생성하여
 * Credential의 기밀성뿐 아니라 저장된 데이터의 변조 여부도 검증할 수 있다.
 */
class AesGcmCredentialCipher(encodedKey: String, private val secureRandom: SecureRandom = SecureRandom(),)
    : CredentialCipher {

    /**
     * 실제 암호화에 사용하는 AES Key.
     *
     * 설정에서는 Binary Key를 직접 다루기 어렵기 때문에
     * Base64 문자열로 전달받은 뒤 AES Key로 변환한다.
     */
    private val secretKey: SecretKeySpec

    init {
        val keyBytes = Base64.getDecoder().decode(encodedKey)

        // AES-256을 사용하므로 반드시 32byte Key가 필요하다.
        require(keyBytes.size == KEY_SIZE_BYTES) {
            "Credential encryption key must be 256 bits."
        }

        secretKey = SecretKeySpec( keyBytes,AES,)
    }

    /**
     * Credential을 AES-GCM으로 암호화한다.
     *
     * GCM에서는 동일한 Key와 IV 조합을 재사용하면 보안이 크게 약화되므로
     * 암호화할 때마다 새로운 IV를 생성한다.
     *
     * 복호화 시 IV가 다시 필요하기 때문에:
     *
     * IV + CipherText + AuthenticationTag
     *
     * 형태의 Payload를 Base64로 인코딩하여 저장한다.
     */
    override fun encrypt( plainText: String, )
    : String {
        val iv = ByteArray(IV_SIZE_BYTES)

        // 같은 AES Key를 계속 사용하더라도 IV는 매 암호화마다 달라야 한다.
        secureRandom.nextBytes(iv)

        val cipher = Cipher.getInstance( TRANSFORMATION, )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            GCMParameterSpec(
                TAG_LENGTH_BITS,
                iv,
            ),
        )

        /*
         * GCM의 doFinal 결과에는 암호문뿐 아니라 Authentication Tag도 포함된다.
         * 이 Tag를 통해 복호화 과정에서 데이터 변조 여부를 검증할 수 있다.
         */
        val encryptedBytes = cipher.doFinal(
            plainText.toByteArray(StandardCharsets.UTF_8,),
        )

        /*
         * IV 자체는 Secret이 아니므로 암호문과 함께 저장한다.
         * 복호화 시 앞의 12byte를 IV로 분리해서 사용한다.
         */
        val payload = iv + encryptedBytes

        /*
         * 향후 암호화 Payload 형식이나 알고리즘이 변경될 수 있으므로
         * DB Schema를 변경하지 않고 기존 암호문 형식을 구분할 수 있도록
         * 저장 값 자체에 암호화 Format Version을 포함한다.
         */
        return "$FORMAT_VERSION:${
            Base64.getEncoder()
                .encodeToString(payload)
        }"
    }

    /**
     * DB에 저장된 암호화 Credential을 원문으로 복원한다.
     *
     * 저장된 Format Version을 확인한 뒤 Payload에서 IV를 분리하고,
     * AES-GCM Authentication Tag 검증과 함께 복호화한다.
     *
     * 암호문이 변조되었거나 다른 Key로 복호화를 시도하면
     * GCM 검증에 실패하여 정상 Credential을 반환하지 않는다.
     */
    override fun decrypt(
        encryptedText: String,
    ): String {
        val (version, encodedPayload) =
            encryptedText.split(
                ":",
                limit = 2,
            ).let {
                require(it.size == 2) {
                    "Invalid encrypted credential format."
                }

                it[0] to it[1]
            }

        // 현재 구현에서 해석할 수 없는 암호화 형식은 임의로 복호화를 시도하지 않는다.
        require(version == FORMAT_VERSION) {
            "Unsupported encrypted credential version: $version"
        }

        val payload = Base64.getDecoder().decode(encodedPayload)

        /*
         * 최소한 IV 이후에 암호화 데이터가 존재해야 한다.
         * 잘못된 형식의 Credential을 암호화 데이터로 처리하지 않도록 검증한다.
         */
        require(payload.size > IV_SIZE_BYTES) {
            "Invalid encrypted credential payload."
        }

        // 암호화 당시 Payload 앞에 저장한 IV를 복원한다.
        val iv = payload.copyOfRange(0, IV_SIZE_BYTES,)
        val encryptedBytes = payload.copyOfRange(IV_SIZE_BYTES, payload.size,)
        val cipher = Cipher.getInstance( TRANSFORMATION,)

        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(
                TAG_LENGTH_BITS,
                iv,
            ),
        )

        /*
         * 복호화와 Authentication Tag 검증이 동시에 수행된다.
         * 저장된 Credential이 변조됐다면 이 단계에서 실패한다.
         */
        val decryptedBytes = cipher.doFinal(encryptedBytes,)

        return String(
            decryptedBytes,
            StandardCharsets.UTF_8,
        )
    }

    companion object {

        private const val AES = "AES"

        /**
         * Padding이 필요 없는 GCM 모드를 사용한다.
         *
         * GCM은 암호화와 데이터 무결성 검증을 함께 제공하는 AEAD 방식이다.
         */
        private const val TRANSFORMATION = "AES/GCM/NoPadding"

        /**
         * credentialData 내부 암호화 형식의 버전.
         *
         * StorageCredential.credentialSchemaVersion과는 별개다.
         *
         * - credentialSchemaVersion: Provider Credential JSON 구조 버전
         * - FORMAT_VERSION: 암호화 Payload 구조 버전
         */
        private const val FORMAT_VERSION = "v1"

        // AES-256 Key 길이: 256bit = 32byte
        private const val KEY_SIZE_BYTES = 32

        /*
         * GCM에서 일반적으로 사용하는 96bit IV.
         * 매 암호화마다 새로운 값이 생성된다.
         */
        private const val IV_SIZE_BYTES = 12

        // Credential 변조 검증에 사용하는 GCM Authentication Tag 길이.
        private const val TAG_LENGTH_BITS = 128
    }
}