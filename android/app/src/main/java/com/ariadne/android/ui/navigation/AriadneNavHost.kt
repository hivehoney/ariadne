package com.ariadne.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ariadne.android.ui.home.HomeScreen
import com.ariadne.android.ui.search.SearchScreen
import com.ariadne.android.ui.storage.StorageScreen

private object Route {
    const val HOME = "home"
    const val SEARCH = "search"
    const val STORAGE = "storage/{storageName}"

    fun storage(storageName: String): String {
        return "storage/$storageName"
    }
}

@Composable
fun AriadneNavHost(modifier: Modifier = Modifier) {
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
            val storageName = backStackEntry.arguments?.getString("storageName")?: "저장공간"

            StorageScreen(
                storageName = storageName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
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