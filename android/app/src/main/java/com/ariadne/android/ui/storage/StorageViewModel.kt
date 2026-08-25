package com.ariadne.android.ui.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariadne.android.data.storage.StorageClient
import com.ariadne.android.data.storage.StorageRepository
import com.ariadne.android.ui.common.model.ConnectionInfoUiModel
import com.ariadne.android.ui.file.model.FileItemUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 외부 Storage 화면의 공통 상태와 데이터 관리
 *
 * StorageRepository의 Local Cache를 화면 데이터 기준으로 관찰하고,
 * Remote 갱신 결과가 Room에 반영되면 UI 상태를 자동 갱신한다.
 */
class StorageViewModel( private val repository: StorageRepository) : ViewModel() {

    // 현재 Storage 화면 상태 관리
    private val _uiState = MutableStateFlow(
        StorageUiState(
            isLoading = true
        )
    )

    // Storage 화면 상태 외부 제공
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    // Local Cache 관찰 작업 관리
    private var observeJob: Job? = null

    // Remote Storage 갱신 작업 관리
    private var refreshJob: Job? = null

    // Local Storage Cache 관찰
    fun load() {
        if (observeJob?.isActive == true) return

        observeJob = viewModelScope.launch {
            repository.observeSnapshot()
                .collect { snapshot ->
                    if (snapshot == null) {
                        return@collect
                    }

                    _uiState.value = StorageUiState(
                        connectionInfo = StorageUiMapper.toConnectionInfo(snapshot),
                        files = StorageUiMapper.toFileItems(snapshot),
                        isLoading = false,
                        errorMessage = _uiState.value.errorMessage
                    )
                }
        }
    }

    // Remote Metadata 조회 및 Local Cache 갱신
    fun refresh( client: StorageClient ) {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            val hasCache = _uiState.value.connectionInfo != null

            if (!hasCache) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                repository.refresh(client)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = if (hasCache) {
                        "최신 Storage 정보를 갱신하지 못했습니다."
                    } else {
                        exception.message
                            ?: "Storage 데이터를 불러오지 못했습니다."
                    }
                )
            }
        }
    }

    // Remote 접근 불가 상태 처리
    fun onRemoteUnavailable( message: String ) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    // 표시 완료된 오류 메시지 제거
    fun consumeError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}

/**
 * Storage 화면에서 사용하는 공통 UI 상태
 */
data class StorageUiState(
    val connectionInfo: ConnectionInfoUiModel? = null,
    val files: List<FileItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)