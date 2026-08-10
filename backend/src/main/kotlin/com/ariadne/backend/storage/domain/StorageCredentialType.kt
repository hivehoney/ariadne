package com.ariadne.backend.storage.domain

/**
 * 외부 Storage 연결에서 사용하는 인증 방식이다.
 *
 * Storage 종류는 StorageSourceType이 표현하고,
 * 이 Enum은 해당 Storage에 접근하기 위한 인증 메커니즘만 표현한다.
 */
enum class StorageCredentialType {
    OAUTH2, // 클라우드
    DEVICE, // 디바이스
}