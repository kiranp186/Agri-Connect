package com.tutorials.agriconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tutorials.agriconnect.ui.theme.AgriconnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriconnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel = androidx.lifecycle.viewmodel.compose.viewModel<AuthViewModel>()

    NavHost(navController = navController, startDestination = "get_started") {
        composable("get_started") {
            GetStartedScreen(
                onGetStartedClick = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginScreen(
                onSignUpClick = { navController.navigate("signup") },
                onLoginClick = {
                    // Navigate to dashboard when login button is clicked
                    navController.navigate("dashboard") {
                        // Clear the back stack so user can't go back to login after successful login
                        popUpTo("login") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable("signup") {
            SignupScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignupSuccess = {
                    // Navigate to dashboard when signup is successful
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        composable("dashboard") {
            FarmersAppScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AgriconnectTheme {
        AppNavigation()
    }
}