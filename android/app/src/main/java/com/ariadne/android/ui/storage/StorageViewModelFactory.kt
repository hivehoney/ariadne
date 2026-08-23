package com.ariadne.android.ui.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ariadne.android.data.storage.StorageClient

/**
 * Provider별 StorageClient를 StorageViewModel에 주입
 *
 * Google Drive와 OneDrive가 서로 다른 Client를 사용하면서도
 * 동일한 StorageViewModel을 재사용할 수 있게 한다.
 */
class StorageViewModelFactory(
    private val client: StorageClient
) : ViewModelProvider.Factory {

    // StorageViewModel 생성
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create( modelClass: Class<T> ): T {
        if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            return StorageViewModel(client) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}