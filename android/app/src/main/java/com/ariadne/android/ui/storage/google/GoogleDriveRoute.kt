package com.ariadne.android.ui.storage.google

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ariadne.android.data.storage.google.DriveClient
import com.ariadne.android.ui.common.model.ConnectionInfoUiModel
import com.ariadne.android.ui.storage.StorageRoute
import com.ariadne.android.ui.storage.StorageScreen

// Google Drive 연결과 공통 Storage 화면 연결
@Composable
fun GoogleDriveRoute(
    storageName: String,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: GoogleAuthViewModel = viewModel()
) {
    // 현재 Android Context 참조
    val context = LocalContext.current

    // 현재 Google 인증 상태 구독
    val authState by authViewModel.authState.collectAsState()

    // 현재 Google 인증 오류 메시지 구독
    val errorMessage by authViewModel.errorMessage.collectAsState()

    // Google 사용자 동의 화면 실행 및 결과 처리
    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            authViewModel.onAuthCancelled()
            return@rememberLauncherForActivityResult
        }

        val data = result.data

        if (data == null) {
            authViewModel.onAuthFailed(
                "Google 인증 결과를 가져오지 못했습니다."
            )
            return@rememberLauncherForActivityResult
        }

        authViewModel.completeAuth(data)
    }

    // Google Drive 화면 최초 진입 시 인증 시작
    LaunchedEffect(Unit) {
        authViewModel.authorize()
    }

    // Google 인증 상태별 UI 동작 처리
    LaunchedEffect(authState) {
        when (val state = authState) {
            is GoogleAuthState.RequiresUserAction -> {
                val request = IntentSenderRequest.Builder(
                    state.pendingIntent.intentSender
                ).build()

                authLauncher.launch(request)
                authViewModel.onUserActionStarted()
            }

            GoogleAuthState.Disconnected -> {
                Toast.makeText(
                    context,
                    "Google Drive 연결이 해제되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                onBackClick()
            }

            else -> Unit
        }
    }

    // Google 인증 오류 메시지 표시
    LaunchedEffect(errorMessage) {
        val message = errorMessage ?: return@LaunchedEffect

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()

        authViewModel.consumeError()
    }

    when (authState) {
        GoogleAuthState.Authorized -> {
            val accessToken = authViewModel.currentAccessToken()

            if (accessToken != null) {
                // Google Drive Storage Client 생성
                val client = remember(accessToken) {
                    DriveClient(accessToken)
                }

                StorageRoute(
                    storageName = storageName,
                    client = client,
                    onBackClick = onBackClick,
                    onSearchClick = onSearchClick,
                    onAccountClick = {
                        // Google 계정 선택 기능 추후 연결
                    },
                    onDisconnectClick = { accountEmail ->
                        authViewModel.disconnect(accountEmail)
                    },
                    modifier = modifier
                )
            }
        }

        is GoogleAuthState.Failed -> {
            // Google 인증 실패 화면 표시
            StorageScreen(
                storageName = storageName,
                files = emptyList(),
                connectionInfo = ConnectionInfoUiModel(
                    title = "Google Drive",
                    account = "연결 실패",
                    detail = ""
                ),
                onBackClick = onBackClick,
                onSearchClick = onSearchClick,
                modifier = modifier
            )
        }

        GoogleAuthState.Disconnected -> {
            // 연결 해제 후 Navigation 완료 대기
        }

        else -> {
            // Google 인증 진행 화면 표시
            StorageScreen(
                storageName = storageName,
                files = emptyList(),
                connectionInfo = ConnectionInfoUiModel(
                    title = "Google Drive",
                    account = "연결 확인 중...",
                    detail = "동기화 준비 중..."
                ),
                isLoading = true,
                onBackClick = onBackClick,
                onSearchClick = onSearchClick,
                modifier = modifier
            )
        }
    }
}