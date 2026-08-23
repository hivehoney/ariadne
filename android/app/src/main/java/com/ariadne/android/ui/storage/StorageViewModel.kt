package com.ariadne.android.ui.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariadne.android.data.storage.StorageClient
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
 * StorageClient를 통해 Provider 종류와 관계없이 데이터를 조회하고,
 * 연결 정보와 파일 목록을 공통 UI 상태로 제공한다.
 */
class StorageViewModel( private val client: StorageClient ) : ViewModel() {

    // 현재 Storage 화면 상태 관리
    private val _uiState = MutableStateFlow(
        StorageUiState(
            isLoading = true
        )
    )

    // Storage 화면 상태 외부 제공
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    // 현재 Storage 조회 작업 관리
    private var loadJob: Job? = null

    // Storage 데이터 조회
    fun load() {
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            runCatching {
                client.loadRoot()
            }.onSuccess { snapshot ->
                _uiState.value = StorageUiState(
                    connectionInfo = StorageUiMapper.toConnectionInfo(snapshot),
                    files = StorageUiMapper.toFileItems(snapshot),
                    isLoading = false
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Storage 데이터를 불러오지 못했습니다."
                )
            }
        }
    }

    // Storage 데이터 재조회
    fun refresh() {
        load()
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