package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.agon.app.data.model.UserProfile
import com.agon.app.ui.screens.AuthScreen
import com.agon.app.ui.screens.ChatScreen
import com.agon.app.ui.screens.ComposioScreen
import com.agon.app.ui.screens.AdminScreen
import com.agon.app.ui.screens.SettingsScreen
import com.agon.app.ui.theme.AgonAppTheme
import com.agon.app.viewmodel.ChatViewModel
import com.agon.app.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val darkTheme by settingsViewModel.darkTheme.collectAsState()

            AgonAppTheme(darkTheme = darkTheme) {
                NovaMindApp()
            }
        }
    }
}

@Composable
fun NovaMindApp(
    chatViewModel: ChatViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val user by chatViewModel.userProfile.collectAsState()
    val startDestination = if (user == null) "auth" else "chat"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthScreen(onLogin = { profile ->
                chatViewModel.login(profile)
                navController.navigate("chat") {
                    popUpTo("auth") { inclusive = true }
                }
            })
        }
        composable("chat") {
            ChatScreen(
                onNavigate = { route ->
                    if (route == "settings" || route == "composio" || route == "admin") {
                        navController.navigate(route)
                    }
                },
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable("composio") {
            ComposioScreen(onBack = { navController.popBackStack() })
        }
        composable("admin") {
            AdminScreen(onBack = { navController.popBackStack() })
        }
    }
}
