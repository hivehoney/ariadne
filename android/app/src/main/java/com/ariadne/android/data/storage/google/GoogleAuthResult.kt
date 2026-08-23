package com.ariadne.android.data.storage.google

import android.app.PendingIntent

/**
 * Google 사용자 권한 요청 결과 표현
 *
 * Google Authorization 결과를 인증 완료와
 * 추가 사용자 동의 필요 상태로 단순화한다.
 */
sealed interface GoogleAuthResult {

    // Google 권한 획득 완료
    data class Authorized(
        val accessToken: String
    ) : GoogleAuthResult

    // Google 사용자 동의 필요
    data class RequiresUserAction(
        val pendingIntent: PendingIntent
    ) : GoogleAuthResult
}