package com.tutorials.agriconnect

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit = {},
    onLoginClick: (String) -> Unit = {}, // Modified to accept the user identifier
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // User input states
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }

    // Loading state
    var isLoading by remember { mutableStateOf(false) }

    // Login method selection (email or phone)
    var useEmailLogin by remember { mutableStateOf(true) }

    // Auth state
    val authState = authViewModel.authState.collectAsState()

    // Handle auth state changes
    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Success -> {
                isLoading = false
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                // Pass the appropriate identifier to the callback
                if (useEmailLogin) {
                    onLoginClick(email)
                } else {
                    onLoginClick(phoneNumber)
                }
            }
            is AuthState.Error -> {
                isLoading = false
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is AuthState.Loading -> {
                isLoading = true
            }
            is AuthState.OtpSent -> {
                isLoading = false
                otpSent = true
                Toast.makeText(context, "OTP sent successfully!", Toast.LENGTH_SHORT).show()
            }
            else -> { /* Idle state, do nothing */ }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Authentication method selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = useEmailLogin,
                onClick = { useEmailLogin = true },
                label = { Text("Email") }
            )

            FilterChip(
                selected = !useEmailLogin,
                onClick = { useEmailLogin = false },
                label = { Text("Phone Number") }
            )
        }

        // Email field (only shown if email login is selected)
        if (useEmailLogin) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
        }

        // Phone number field (only shown if phone login is selected)
        if (!useEmailLogin) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) phoneNumber = it
                    },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (phoneNumber.length == 10) {
                            // Find the activity context properly - critical fix
                            val currentActivity = context as? ComponentActivity
                            if (currentActivity != null) {
                                coroutineScope.launch {
                                    authViewModel.sendPhoneVerificationCode(currentActivity, phoneNumber)
                                }
                            } else {
                                Toast.makeText(context, "Cannot perform this operation in the current context", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = phoneNumber.length == 10 && !isLoading,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Get OTP")
                }
            }

            // OTP Field (visible only after "Get OTP" is clicked)
            if (otpSent) {
                Text(
                    text = "Enter OTP sent to your phone",
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            otp = it
                        }
                    },
                    label = { Text("Enter 6-digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                )
            }
        }

        // Password field (only for email login)
        if (useEmailLogin) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "👁" else "👁‍🗨",
                            fontSize = 18.sp
                        )
                    }
                }
            )
        }

        Button(
            onClick = {
                Log.d("LoginScreen", "Login button clicked")
                coroutineScope.launch {
                    try {
                        if (useEmailLogin) {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            Log.d("LoginScreen", "Calling loginWithEmail")
                            authViewModel.loginWithEmail(email, password)
                        } else {
                            if (phoneNumber.isBlank() || otp.isBlank()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            Log.d("LoginScreen", "Calling verifyPhoneCode")
                            // We're passing empty user data because existing users already have data stored
                            authViewModel.verifyPhoneCode(otp, mapOf())
                        }
                    } catch (e: Exception) {
                        Log.e("LoginScreen", "Exception in login button click: ${e.message}", e)
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(4.dp),
            enabled = !isLoading && (
                    (useEmailLogin && email.isNotBlank() && password.isNotBlank()) ||
                            (!useEmailLogin && phoneNumber.isNotBlank() && otp.isNotBlank() && otpSent)
                    )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { /* Handle forgot password */ }) {
            Text("Forgot Password?")
        }

        // Sign Up button now with navigation
        TextButton(onClick = onSignUpClick) {
            Text("Don't have an account? Sign Up")
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    MaterialTheme {
        LoginScreen()
    }
}