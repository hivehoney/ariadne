package com.ariadne.backend.storage.credential

import kotlin.reflect.KClass
import tools.jackson.databind.ObjectMapper

/**
 * CredentialData를 JSON으로 직렬화한 뒤 암호화하여 저장하는 Codec이다.
 *
 * 저장:
 * CredentialData -> JSON -> Encrypt
 *
 * 조회:
 * Encrypted Data -> Decrypt -> JSON -> CredentialData
 *
 * 직렬화와 암호화를 하나의 컴포넌트로 묶어
 * 호출부에서 암호화를 누락하는 실수를 방지한다.
 */
class JsonEncryptedCredentialDataCodec(private val objectMapper: ObjectMapper, private val credentialCipher: CredentialCipher,)
    : CredentialDataCodec {

    override fun encode( credentialData: StorageCredentialData,): String {
        /*
         * Provider CredentialData를 먼저 JSON으로 변환한다.
         *
         * JSON 자체에는 Refresh Token이 포함될 수 있으므로
         * 이 값을 그대로 Persistence 계층에 전달하지 않고
         * 즉시 CredentialCipher를 통해 암호화한다.
         */
        val serialized = objectMapper.writeValueAsString(credentialData,)

        return credentialCipher.encrypt(
            serialized,
        )
    }

    override fun <T : StorageCredentialData> decode(encodedData: String, type: KClass<T>,): T {
        /*
         * DB에는 암호화된 Credential만 존재하므로
         * Provider 타입으로 변환하기 전에 먼저 복호화한다.
         */
        val serialized = credentialCipher.decrypt(encodedData,)

        return objectMapper.readValue(
            serialized,
            type.java,
        )
    }
}