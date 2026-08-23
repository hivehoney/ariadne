package com.ariadne.android.data.storage

import com.ariadne.android.data.storage.model.StorageSnapshot

/**
 * 외부 Storage 데이터 조회를 위한 공통 인터페이스
 *
 * Provider별 API 차이를 상위 계층에서 숨기고,
 * Storage 화면에서 필요한 연결 정보와 파일 목록을 동일한 방식으로 제공한다.
 */
interface StorageClient {

    // Storage 루트 데이터 조회
    suspend fun loadRoot(): StorageSnapshot
}