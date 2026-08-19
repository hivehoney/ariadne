package com.ariadne.backend.storage.credential

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ObjectMapper

/**
 * Credential 암호화와 직렬화에 필요한 Bean을 구성한다.
 *
 * 암호화 Key는 코드에 저장하지 않고 외부 설정에서 전달받는다.
 */
@Configuration(proxyBeanMethods = false)
class CredentialConfiguration(
    @Value("\${ariadne.credential.encryption-key}")
    private val encryptionKey: String,
) {

    @Bean
    fun credentialCipher(): CredentialCipher {
        return AesGcmCredentialCipher(encryptionKey)
    }

    @Bean
    fun credentialDataCodec(
        objectMapper: ObjectMapper,
        credentialCipher: CredentialCipher,
    ): CredentialDataCodec {
        return JsonEncryptedCredentialDataCodec(objectMapper, credentialCipher)
    }
}