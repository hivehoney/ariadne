package com.ariadne.android.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ariadne.android.data.BackendRepository

enum class BackendConnectionState {
    CHECKING,
    CONNECTED,
    FAILED
}

class BackendConnectionViewModel : ViewModel() {

    private val repository = BackendRepository()

    var connectionState by mutableStateOf(BackendConnectionState.CHECKING)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun checkConnection() {
        connectionState = BackendConnectionState.CHECKING
        errorMessage = null

        repository.checkHealth(
            onSuccess = {
                connectionState = BackendConnectionState.CONNECTED
            },
            onFailure = { error ->
                errorMessage = error.message ?: error.javaClass.simpleName
                connectionState = BackendConnectionState.FAILED
            }
        )
    }
}