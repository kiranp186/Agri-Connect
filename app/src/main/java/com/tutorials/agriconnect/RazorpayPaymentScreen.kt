package com.tutorials.agriconnect

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

@Composable
fun RazorpayPaymentScreen(
    navController: NavController,
    amount: Double,
    equipmentName: String
) {
    // Get the current context
    val context = LocalContext.current

    // Payment status state
    var paymentStatus by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Format the amount for display
    val formattedAmount = "₹ ${amount.toInt()}"

    // Initialize Razorpay checkout on component mount
    LaunchedEffect(Unit) {
        Checkout.preload(context)
    }

    // Function to start Razorpay payment
    fun startPayment() {
        isLoading = true

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_1YCEXIOK6PSYZE" ) // Replace with your actual Razorpay key

        val options = JSONObject()
        try {
            options.put("name", "AgriConnect")
            options.put("description", "Booking for $equipmentName")
            options.put("currency", "INR")
            options.put("amount", (amount * 100).toInt()) // Amount in smallest currency unit (paise)
            options.put("prefill.email", "customer@example.com")
            options.put("prefill.contact", "9876543210")

            val activity = context as android.app.Activity
            checkout.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(context, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Payment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Payment Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Booking Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Equipment",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = equipmentName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Amount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedAmount,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }

        // Payment Method Section
        Text(
            text = "Payment Method",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Razorpay Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Razorpay",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                RadioButton(
                    selected = true,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF4CAF50)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Payment status display
        paymentStatus?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (it.contains("success", ignoreCase = true))
                            Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        color = if (it.contains("success", ignoreCase = true))
                            Color(0xFF4CAF50) else Color(0xFFE53935)
                    )
                }

                if (it.contains("success", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            // Navigate back to the equipment page or desired screen
                            navController.navigate("equipment_page") {
                                popUpTo("razorpay_payment_screen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text(
                            text = "Done",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Pay Now Button
        Button(
            onClick = { startPayment() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Pay $formattedAmount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Handle Razorpay payment result
    // In a real app, you would implement the PaymentResultListener interface in your Activity
    // This is a simplified version for demonstration purposes
    DisposableEffect(Unit) {
        val listener = object : PaymentResultListener {
            override fun onPaymentSuccess(razorpayPaymentId: String?) {
                paymentStatus = "Payment Successful! ID: $razorpayPaymentId"
                isLoading = false

                // Navigate to booking confirmation after successful payment
                // You can add this to navigate to a confirmation screen
                // navController.navigate("booking_confirmation")
            }

            override fun onPaymentError(code: Int, description: String?) {
                paymentStatus = "Payment Failed: $description"
                isLoading = false
            }
        }

        // In a real app, you would register this listener with your Activity

        onDispose {
            // Clean up if needed
        }
    }
}

@Composable
fun PaymentSuccessScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Payment Successful",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("equipment_page") {
                    popUpTo("payment_success_screen") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Text(
                text = "Done",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}