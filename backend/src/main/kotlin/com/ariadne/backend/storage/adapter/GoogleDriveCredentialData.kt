package com.ariadne.backend.storage.adapter

import com.ariadne.backend.storage.credential.StorageCredentialData

/**
 * Google Drive 연결을 유지하기 위해 저장해야 하는 Provider 전용 인증정보다.
 *
 * Access Token은 단기 Token이므로 영구 저장하지 않고,
 * 이후 Google API 호출 시 필요한 Refresh Token만 보관한다.
 */
data class GoogleDriveCredentialData(val refreshToken: String, ) : StorageCredentialData {

    init {
        require(refreshToken.isNotBlank()) {
            "Google Drive refresh token must not be blank."
        }
    }

    companion object {

        /**
         * StorageCredential.credentialSchemaVersion에 저장할
         * Google Drive Credential Payload 구조의 현재 버전이다.
         */
        const val SCHEMA_VERSION = 1
    }
}