package com.tutorials.agriconnect

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.firestore.FirebaseFirestore
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

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val authViewModel = androidx.lifecycle.viewmodel.compose.viewModel<AuthViewModel>()
        val context = LocalContext.current

        NavHost(navController = navController, startDestination = "get_started") {
            composable("get_started") {
                GetStartedScreen(
                    onGetStartedClick = { navController.navigate("login") }
                )
            }

            composable("login") {
                LoginScreen(
                    onSignUpClick = { navController.navigate("signup") },
                    onLoginClick = { email ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    val document = querySnapshot.documents[0]
                                    val userType = document.getString("userType")

                                    when (userType) {
                                        "Farmer" -> {
                                            navController.navigate("dashboard") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }

                                        "Equipment Provider" -> {
                                            navController.navigate("owner_dashboard") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }

                                        else -> {
                                            Toast.makeText(
                                                context,
                                                "Unknown user type: $userType",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    authViewModel = authViewModel
                )
            }

            composable("signup") {
                SignupScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSignupComplete = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    authViewModel = authViewModel
                )
            }

            composable("dashboard") {
                FarmersAppScreen(navController = navController)
            }

            composable("owner_dashboard") {
                OwnerAppScreen(navController = navController)
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

            composable(
                "crop_specific/{cropName}",
                arguments = listOf(navArgument("cropName") { type = NavType.StringType })
            ) { backStackEntry ->
                val cropName = backStackEntry.arguments?.getString("cropName") ?: "Unknown Crop"
                CropSpecificScreen(
                    navController = navController,
                    cropName = cropName,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("equipment_detail/{equipmentId}") { backStackEntry ->
                val equipmentId = backStackEntry.arguments?.getString("equipmentId") ?: "0"
                EquipmentDetailPage(
                    equipmentId = equipmentId,
                    navController = navController
                )
            }

            composable("equipment_list") {
                equipmentlist(navController = navController)
            }

            composable(
                "specific_category/{categoryName}",
                arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryName =
                    backStackEntry.arguments?.getString("categoryName") ?: "Unknown Category"
                SpecificCategoryScreen(
                    category = categoryName,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("payment_screen") {
                RazorpayPaymentScreen(
                    navController = navController,
                    amount = 5000.0,
                    equipmentName = "John Deere 6135E-135 HP Tractor"
                )
            }

            composable(
                "owner_dashboard?equipment_added={equipment_added}",
                arguments = listOf(navArgument("equipment_added") {
                    type = NavType.StringType
                    defaultValue = "false"
                })
            ) { backStackEntry ->
                val equipmentAdded = backStackEntry.arguments?.getString("equipment_added")?.toBoolean() ?: false
                OwnerAppScreen(
                    navController = navController,
                    showEquipmentAddedMessage = equipmentAdded
                )
            }

            composable("add_equipment") {
                EquipmentOwnerDashboardScreen(navController = navController)
            }
        }
    }
}