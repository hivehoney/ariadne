package com.ariadne.android.data.storage.google

import android.accounts.Account
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task

/**
 * Google Drive 접근을 위한 Google Authorization 처리
 *
 * Drive 읽기 권한과 Access Token 획득을 담당하며,
 * 연결 해제 시 해당 Google 계정에 부여된 앱 접근 권한을 취소한다.
 */
class GoogleAuthClient( context: Context ) {

    // Google Authorization Client 관리
    private val client = Identity.getAuthorizationClient(context)

    // Google Drive 읽기 권한 요청
    fun authorize(): Task<GoogleAuthResult> {
        val request = AuthorizationRequest.builder()
            .setRequestedScopes(
                listOf(
                    Scope(DRIVE_READONLY_SCOPE)
                )
            )
            .build()

        return client.authorize(request)
            .continueWith { task ->
                val result = task.getResult(Exception::class.java)

                toResult(result)
            }
    }

    // Google 사용자 동의 결과 처리
    fun completeAuth( data: Intent ): GoogleAuthResult.Authorized {
        val result = client.getAuthorizationResultFromIntent(data)

        return GoogleAuthResult.Authorized(
            accessToken = requireAccessToken(result)
        )
    }

    // Google Drive 연결 권한 취소
    fun revokeAccess( accountEmail: String ): Task<Void> {
        val account = Account(
            accountEmail,
            GOOGLE_ACCOUNT_TYPE
        )

        val request = RevokeAccessRequest.builder()
            .setAccount(account)
            .setScopes(
                listOf(
                    Scope(DRIVE_READONLY_SCOPE)
                )
            )
            .build()

        return client.revokeAccess(request)
    }

    // Google Authorization 결과 변환
    private fun toResult( result: AuthorizationResult ): GoogleAuthResult {
        if (result.hasResolution()) {
            return GoogleAuthResult.RequiresUserAction(
                pendingIntent = requireNotNull(result.pendingIntent)
            )
        }

        return GoogleAuthResult.Authorized(
            accessToken = requireAccessToken(result)
        )
    }

    // Google Access Token 추출
    private fun requireAccessToken( result: AuthorizationResult ): String {
        return result.accessToken
            ?.takeIf { it.isNotBlank() }
            ?: error("Google Access Token을 가져오지 못했습니다.")
    }

    companion object {

        // Google 계정 유형
        private const val GOOGLE_ACCOUNT_TYPE = "com.google"

        // Google Drive 파일 조회 및 다운로드 권한
        private const val DRIVE_READONLY_SCOPE = "https://www.googleapis.com/auth/drive.readonly"
    }
}