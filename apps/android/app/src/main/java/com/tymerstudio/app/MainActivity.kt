package com.tymerstudio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tymerstudio.app.ui.screens.ChatScreen
import com.tymerstudio.app.ui.screens.ConnectionScreen
import com.tymerstudio.app.ui.screens.SettingsScreen
import com.tymerstudio.app.ui.theme.TymerStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TymerStudioTheme {
                TymerStudioApp()
            }
        }
    }
}

@Composable
fun TymerStudioApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = "connection"
        ) {
            composable("connection") {
                ConnectionScreen(
                    onConnected = {
                        navController.navigate("chat") {
                            popUpTo("connection") { inclusive = true }
                        }
                    },
                    onOpenSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            composable("chat") {
                ChatScreen(
                    onDisconnect = {
                        navController.navigate("connection") {
                            popUpTo("chat") { inclusive = true }
                        }
                    },
                    onOpenSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
