package com.ariadne.android.ui.storage.google

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.ariadne.android.data.storage.google.GoogleAuthClient
import com.ariadne.android.data.storage.google.GoogleAuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Google Drive 연결을 위한 Google 인증 상태 관리
 *
 * Google 권한 요청과 Access Token 획득 및 연결 해제를 담당하며,
 * 실제 Storage 데이터 처리는 공통 Storage 구조에 위임한다.
 */
class GoogleAuthViewModel( application: Application ) : AndroidViewModel(application) {

    // Google 인증 Client 관리
    private val authClient = GoogleAuthClient(application.applicationContext)

    // 현재 Google 인증 상태 관리
    private val _authState = MutableStateFlow<GoogleAuthState>(GoogleAuthState.Idle)

    // Google 인증 상태 외부 제공
    val authState: StateFlow<GoogleAuthState> = _authState.asStateFlow()

    // Google 인증 오류 메시지 관리
    private val _errorMessage = MutableStateFlow<String?>(null)

    // Google 인증 오류 메시지 외부 제공
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Google Drive API 호출용 Access Token 관리
    private var accessToken: String? = null

    // Google Drive 접근 권한 요청
    fun authorize() {
        if (_authState.value is GoogleAuthState.Authorizing) return
        if (_authState.value is GoogleAuthState.Authorized) return

        _authState.value = GoogleAuthState.Authorizing

        authClient.authorize()
            .addOnSuccessListener { result ->
                when (result) {
                    is GoogleAuthResult.Authorized -> {
                        accessToken = result.accessToken
                        _authState.value = GoogleAuthState.Authorized
                    }

                    is GoogleAuthResult.RequiresUserAction -> {
                        _authState.value = GoogleAuthState.RequiresUserAction(
                            result.pendingIntent
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                fail(
                    exception.message ?: "Google 인증에 실패했습니다."
                )
            }
    }

    // Google 사용자 동의 결과 처리
    fun completeAuth( data: Intent ) {
        runCatching {
            authClient.completeAuth(data)
        }.onSuccess { result ->
            accessToken = result.accessToken
            _authState.value = GoogleAuthState.Authorized
        }.onFailure { exception ->
            fail(
                exception.message ?: "Google 인증 결과 처리에 실패했습니다."
            )
        }
    }

    // Google Drive 연결 권한 해제
    fun disconnect( accountEmail: String ) {
        if (accountEmail.isBlank()) {
            _errorMessage.value = "Google 계정 정보를 찾을 수 없습니다."
            return
        }

        authClient.revokeAccess(accountEmail)
            .addOnSuccessListener {
                accessToken = null
                _authState.value = GoogleAuthState.Disconnected
            }
            .addOnFailureListener { exception ->
                _errorMessage.value =
                    exception.message ?: "Google Drive 연결 해제에 실패했습니다."
            }
    }

    // Google 사용자 동의 화면 실행 상태 처리
    fun onUserActionStarted() {
        _authState.value = GoogleAuthState.Authorizing
    }

    // Google 인증 취소 처리
    fun onAuthCancelled() {
        fail("Google 인증이 취소되었습니다.")
    }

    // Google 인증 실패 처리
    fun onAuthFailed( message: String ) {
        fail(message)
    }

    // 현재 Google Access Token 제공
    fun currentAccessToken(): String? {
        return accessToken
    }

    // Google 인증 실패 상태 처리
    private fun fail( message: String ) {
        accessToken = null
        _authState.value = GoogleAuthState.Failed(message)
        _errorMessage.value = message
    }

    // 표시 완료된 오류 메시지 제거
    fun consumeError() {
        _errorMessage.value = null
    }
}