package com.ariadne.android.ui.storage.google

import android.app.PendingIntent

/**
 * Google Drive 연결 과정의 인증 상태 표현
 *
 * Google 전용 사용자 동의 흐름만 표현하며,
 * Storage 데이터 조회 상태와는 분리한다.
 */
sealed interface GoogleAuthState {

    // Google 인증 시작 전 상태
    data object Idle : GoogleAuthState

    // Google 인증 진행 상태
    data object Authorizing : GoogleAuthState

    // Google 사용자 동의 필요 상태
    data class RequiresUserAction(
        val pendingIntent: PendingIntent
    ) : GoogleAuthState

    // Google 인증 완료 상태
    data object Authorized : GoogleAuthState

    // Google 연결 해제 완료 상태
    data object Disconnected : GoogleAuthState

    // Google 인증 실패 상태
    data class Failed(
        val message: String
    ) : GoogleAuthState
}