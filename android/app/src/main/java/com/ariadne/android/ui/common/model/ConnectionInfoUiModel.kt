package com.ariadne.android.ui.common.model

/**
 * 외부 서비스의 연결 정보를 화면에 표시하기 위한 모델이다.
 *
 * 서비스 이름, 연결 계정과 상태 정보를 공통 형태로 전달하여
 * Google Drive, OneDrive 등 여러 외부 서비스에서 재사용한다.
 */
data class ConnectionInfoUiModel(
    val title: String,
    val account: String,
    val detail: String
)