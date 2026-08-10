package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.domain.StorageSourceType

/**
 * 외부 Storage 연결 구현체의 공통 진입점이다.
 *
 * Provider마다 실제 연결 방식은 다르지만 type을 기준으로
 * 적절한 Connector를 선택할 수 있도록 공통 식별 정보를 제공한다.
 */
interface StorageConnector {

    val type: StorageSourceType
}