package com.ariadne.android.ui.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ariadne.android.data.storage.StorageRepository

/**
 * StorageRepository를 StorageViewModel에 주입
 *
 * Provider별 Local/Remote 데이터 정책을 공통 StorageViewModel에서
 * 동일한 방식으로 사용할 수 있게 한다.
 */
class StorageViewModelFactory(
    private val repository: StorageRepository
) : ViewModelProvider.Factory {

    // StorageViewModel 생성
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create( modelClass: Class<T> ): T {
        if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            return StorageViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}