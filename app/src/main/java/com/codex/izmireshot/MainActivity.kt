package com.codex.izmireshot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codex.izmireshot.feature.announcements.AnnouncementsScreen
import com.codex.izmireshot.feature.favorites.FavoritesScreen
import com.codex.izmireshot.feature.home.HomeScreen
import com.codex.izmireshot.feature.line_detail.LineDetailScreen
import com.codex.izmireshot.feature.nearby.NearbyScreen
import com.codex.izmireshot.feature.stop_detail.StopDetailScreen
import com.codex.izmireshot.ui.theme.IzmirEshotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IzmirEshotTheme {
                EshotApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EshotApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("İzmir ESHOT") })
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding),
        ) {
            composable("home") {
                HomeScreen(
                    onLineClick = { navController.navigate("line/$it") },
                    onStopClick = { navController.navigate("stop/$it") },
                    onNearbyClick = { navController.navigate("nearby") },
                    onFavoritesClick = { navController.navigate("favorites") },
                    onAnnouncementsClick = { navController.navigate("announcements") },
                )
            }
            composable(
                route = "line/{lineNo}",
                arguments = listOf(navArgument("lineNo") { type = NavType.IntType }),
            ) {
                LineDetailScreen(onStopClick = { navController.navigate("stop/$it") })
            }
            composable(
                route = "stop/{stopId}",
                arguments = listOf(navArgument("stopId") { type = NavType.IntType }),
            ) {
                StopDetailScreen(onLineClick = { navController.navigate("line/$it") })
            }
            composable("nearby") {
                NearbyScreen(onStopClick = { navController.navigate("stop/$it") })
            }
            composable("favorites") {
                FavoritesScreen(
                    onLineClick = { navController.navigate("line/$it") },
                    onStopClick = { navController.navigate("stop/$it") },
                )
            }
            composable("announcements") {
                AnnouncementsScreen()
            }
        }
    }
}
