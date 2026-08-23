package com.ariadne.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ariadne.android.ui.home.HomeScreen
import com.ariadne.android.ui.search.SearchScreen
import com.ariadne.android.ui.storage.StorageScreen
import com.ariadne.android.ui.storage.google.GoogleDriveRoute

/**
 * Ariadne 최상위 화면 Navigation 관리
 *
 * Home, Search, Storage 화면 이동을 연결하고,
 * Provider별 연결이 필요한 경우 전용 Route로 진입시킨다.
 */
private object Route {
    const val HOME = "home"
    const val SEARCH = "search"
    const val STORAGE = "storage/{storageName}"

    // Storage 화면 Route 생성
    fun storage( storageName: String ): String {
        return "storage/$storageName"
    }
}

/**
 * 현재 지원하는 Storage 화면 이름 관리
 */
private object StorageName {
    const val GOOGLE_DRIVE = "Google Drive"
}

@Composable
fun AriadneNavHost( modifier: Modifier = Modifier ) {
    // Ariadne Navigation 상태 관리
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.HOME,
        modifier = modifier
    ) {
        composable(Route.HOME) {
            HomeScreen(
                onSearchClick = {
                    navController.navigate(Route.SEARCH)
                },
                onStorageClick = { storageName ->
                    navController.navigate(Route.storage(storageName))
                }
            )
        }

        composable(Route.STORAGE) { backStackEntry ->
            val storageName =
                backStackEntry.arguments?.getString("storageName") ?: "저장공간"

            val onBackClick: () -> Unit = {
                navController.popBackStack()
            }

            val onSearchClick: () -> Unit = {
                navController.navigate(Route.SEARCH)
            }

            if (storageName == StorageName.GOOGLE_DRIVE) {
                GoogleDriveRoute(
                    storageName = storageName,
                    onBackClick = onBackClick,
                    onSearchClick = onSearchClick
                )
            } else {
                StorageScreen(
                    storageName = storageName,
                    onBackClick = onBackClick,
                    onSearchClick = onSearchClick
                )
            }
        }

        composable(Route.SEARCH) {
            SearchScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}