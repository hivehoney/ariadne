package com.ariadne.backend.storage.credential

/**
 * Storage 인증정보의 암호화/복호화를 추상화한다.
 *
 * Google Drive, OneDrive 등 Provider는 실제 암호화 알고리즘을 알 필요 없이
 * 이 인터페이스를 통해 Credential을 안전하게 저장하고 복원한다.
 *
 * 현재는 AES-GCM을 사용하지만 향후 Vault/KMS 등으로 암호화 방식이 변경되어도
 * Credential을 사용하는 코드가 영향을 받지 않도록 암호화 책임을 분리한다.
 */
interface CredentialCipher {

    /**
     * DB에 저장하기 전에 평문 Credential을 암호화한다.
     */
    fun encrypt(plainText: String,): String

    /**
     * 외부 Storage API 호출에 사용하기 위해 저장된 Credential을 복호화한다.
     */
    fun decrypt(encryptedText: String,): String
}