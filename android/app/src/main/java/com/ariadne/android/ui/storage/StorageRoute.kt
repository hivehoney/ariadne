package com.ariadne.android.ui.storage

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ariadne.android.data.storage.StorageClient
import com.ariadne.android.data.storage.StorageRepository
import com.ariadne.android.ui.common.model.ConnectionInfoUiModel

/**
 * 외부 Storage 공통 화면 진입 흐름 관리
 *
 * Local Cache는 즉시 관찰하고,
 * Provider 인증 완료 후 전달된 StorageClient로 Remote 갱신을 수행한다.
 */
@Composable
fun StorageRoute(
    storageName: String,
    repository: StorageRepository,
    client: StorageClient?,
    remoteErrorMessage: String? = null,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAccountClick: () -> Unit = {},
    onDisconnectClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 현재 Android Context 참조
    val context = LocalContext.current

    // Storage ViewModel Factory 관리
    val factory = remember(repository) {
        StorageViewModelFactory(repository)
    }

    // 현재 Storage ViewModel 관리
    val viewModel: StorageViewModel = viewModel(
        key = "storage-${storageName}-${repository.hashCode()}",
        factory = factory
    )

    // 현재 Storage 화면 상태 구독
    val state by viewModel.uiState.collectAsState()

    // Storage 화면 진입 즉시 Local Cache 조회
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    // Provider 인증 완료 후 Remote Metadata 갱신
    LaunchedEffect(client) {
        client?.let { storageClient ->
            viewModel.refresh(storageClient)
        }
    }

    // Provider Remote 접근 실패 처리
    LaunchedEffect(remoteErrorMessage) {
        remoteErrorMessage?.let { message ->
            viewModel.onRemoteUnavailable(message)
        }
    }

    // Storage 오류 메시지 표시
    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()

        viewModel.consumeError()
    }

    // Storage 화면 데이터 표시
    StorageScreen(
        storageName = storageName,
        files = state.files,
        connectionInfo = state.connectionInfo
            ?: ConnectionInfoUiModel(
                title = storageName,
                account = if (state.isLoading) "연결 확인 중..." else "",
                detail = if (state.isLoading) "동기화 준비 중..." else ""
            ),
        isLoading = state.isLoading,
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onAccountClick = onAccountClick,
        onDisconnectClick = {
            val account = state.connectionInfo?.account

            if (!account.isNullOrBlank()) {
                onDisconnectClick(account)
            }
        },
        modifier = modifier
    )
}