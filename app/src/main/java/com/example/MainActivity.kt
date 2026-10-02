package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.CommandViewModel
import com.example.ui.HistoryScreen
import com.example.ui.HomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CommandViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToFavorites = { navController.navigate("favorites") }
                        )
                    }
                    composable("history") {
                        HistoryScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            isFavoritesOnly = false
                        )
                    }
                    composable("favorites") {
                        HistoryScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            isFavoritesOnly = true
                        )
                    }
                }
            }
        }
    }
}
