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
import com.ariadne.android.data.storage.StorageRepository
import com.ariadne.android.data.storage.google.DriveClient
import com.ariadne.android.data.storage.local.AriadneDatabaseProvider
import com.ariadne.android.data.storage.model.StorageProviderType
import com.ariadne.android.ui.storage.StorageRoute

/**
 * Google Drive 인증과 공통 Storage 화면 연결
 *
 * Local Cache를 먼저 표시하고 Google 인증이 완료되면
 * DriveClient를 통해 최신 Metadata를 갱신한다.
 */
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

    // Ariadne Local Database 관리
    val database = remember {
        AriadneDatabaseProvider.getDatabase(
            context.applicationContext
        )
    }

    // Google Drive Storage Repository 관리
    val repository = remember(database) {
        StorageRepository(
            cacheDao = database.storageCacheDao(),
            providerType = StorageProviderType.GOOGLE_DRIVE
        )
    }

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

    // Google 인증 상태별 동작 처리
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
                // 연결 해제 시 Local Metadata Cache 제거
                repository.clearCache()

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

    // 인증 완료 시에만 Google Remote Client 생성
    val client = if (authState == GoogleAuthState.Authorized) {
        authViewModel.currentAccessToken()?.let { accessToken ->
            remember(accessToken) {
                DriveClient(accessToken)
            }
        }
    } else {
        null
    }

    // 인증 실패 시에도 Local Cache 화면은 유지
    val remoteErrorMessage =
        (authState as? GoogleAuthState.Failed)?.message

    StorageRoute(
        storageName = storageName,
        repository = repository,
        client = client,
        remoteErrorMessage = remoteErrorMessage,
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