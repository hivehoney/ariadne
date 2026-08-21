package com.ariadne.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ariadne.android.ui.BackendConnectionState
import com.ariadne.android.ui.BackendConnectionViewModel
import com.ariadne.android.ui.navigation.AriadneNavHost
import com.ariadne.android.ui.theme.AriadneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AriadneTheme {
                val backendViewModel: BackendConnectionViewModel = viewModel()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    backendViewModel.checkConnection()
                }

                LaunchedEffect(backendViewModel.connectionState) {
                    when (backendViewModel.connectionState) {
                        BackendConnectionState.CONNECTED -> {
                            snackbarHostState.showSnackbar("백엔드 연결됨")
                        }

                        BackendConnectionState.FAILED -> {
                            snackbarHostState.showSnackbar(
                                "백엔드 연결 실패: ${backendViewModel.errorMessage}"
                            )
                        }

                        BackendConnectionState.CHECKING -> Unit
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { innerPadding ->
                    AriadneNavHost(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}