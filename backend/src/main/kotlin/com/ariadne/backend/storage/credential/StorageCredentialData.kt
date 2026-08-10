package com.ariadne.backend.storage.credential

/**
 * Provider별 Credential Data가 구현하는 공통 타입이다.
 *
 * Google Drive, OneDrive 등 Provider마다 실제 인증정보 구조는 다르지만
 * Storage Credential 저장 과정에서는 동일한 Codec을 사용할 수 있도록 한다.
 */
interface StorageCredentialData