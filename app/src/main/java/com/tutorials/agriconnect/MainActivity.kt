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
                    AppNavigation()       }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

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
                }
            )
        }

        composable("signup") {
            SignupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignupComplete = {
                    // Navigate to dashboard when signup is complete
                    navController.navigate("dashboard") {
                        // Clear the back stack so user can't go back to signup/login after successful signup
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            FarmersAppScreen(navController = navController)
        }

        composable("categories") {
            FarmTechHomeScreen().FarmTechApp(navController = navController)
        }

        composable("my_bookings") {
            MyBookings(navController = navController)
        }

        composable("account") {
            FarmerProfileScreen(navController = navController)
        }



        composable("crop_specific") {
            // Add your crop specific screen here
            // For now, let's just reuse FarmersAppScreen as a placeholder
            FarmersAppScreen(navController = navController)
        }




    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AgriconnectTheme {
        EquipmentDetailPage()
    }
}}