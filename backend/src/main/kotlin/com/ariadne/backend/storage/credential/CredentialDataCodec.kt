package com.ariadne.backend.storage.credential

import kotlin.reflect.KClass

/**
 * Provider별 CredentialData를 DB 저장 가능한 형태로 변환하고 복원한다.
 *
 * 호출하는 Provider가 직렬화와 암호화를 각각 처리하지 않도록
 * 두 과정을 하나의 경계로 묶는다.
 *
 * 이를 통해 평문 Credential이 실수로 DB에 저장되는 것을 방지한다.
 */
interface CredentialDataCodec {

    /**
     * Provider CredentialData를 직렬화하고 암호화하여
     * StorageCredential.credentialData에 저장할 문자열을 만든다.
     */
    fun encode( credentialData: StorageCredentialData,): String

    /**
     * StorageCredential에 저장된 암호문을 복호화하고 역직렬화하여
     * Provider 전용 CredentialData로 복원한다.
     */
    fun <T : StorageCredentialData> decode(encodedData: String, type: KClass<T>,): T
}