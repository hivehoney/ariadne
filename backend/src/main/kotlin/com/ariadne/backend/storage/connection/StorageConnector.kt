package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.domain.StorageSourceType

/**
 * 외부 Storage 연결 구현체의 공통 규약
 */
interface StorageConnector {

    /** 이 Connector가 담당하는 Storage 종류 */
    val type: StorageSourceType

    /**
     * Provider별 연결 요청을 처리하고 공통 연결 결과로 변환한다.
     */
    fun connect(request: StorageConnectionRequest): StorageConnectionResult
}