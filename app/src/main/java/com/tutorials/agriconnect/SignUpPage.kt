package com.tutorials.agriconnect

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

class SignUpPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SignupScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onNavigateBack: () -> Unit = {},
    onSignupComplete: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State variables for form fields
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Authentication method selection
    var useEmailAuth by remember { mutableStateOf(true) }

    // User type selection
    var expanded by remember { mutableStateOf(false) }
    var selectedUserType by remember { mutableStateOf("Select User Type") }
    val userTypes = listOf("Farmer", "Equipment Provider")

    // Location details
    var state by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var taluk by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Auth state
    val authState = authViewModel.authState.collectAsState()

    // Handle auth state changes
    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is AuthState.Success -> {
                isLoading = false
                Toast.makeText(context, "Signup successful!", Toast.LENGTH_SHORT).show()
                onSignupComplete()
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Authentication method selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = useEmailAuth,
                onClick = { useEmailAuth = true },
                label = { Text("Email") }
            )

            FilterChip(
                selected = !useEmailAuth,
                onClick = { useEmailAuth = false },
                label = { Text("Phone Number") }
            )
        }

        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Email field (only shown if email auth is selected)
        if (useEmailAuth) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
        }

        // Phone number field (only shown if phone auth is selected)
        if (!useEmailAuth) {
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true
                )

                // Find this code in your SignupScreen composable
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

        // Password fields (only shown for email auth)
        if (useEmailAuth) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                isError = password != confirmPassword && confirmPassword.isNotEmpty()
            )

            if (password != confirmPassword && confirmPassword.isNotEmpty()) {
                Text(
                    text = "Passwords do not match",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp)
                )
            }
        }

        // User Type Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedUserType,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                label = { Text("User Type") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                userTypes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedUserType = option
                            expanded = false
                        }
                    )
                }
            }
        }

        // State field
        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("State") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // District and Taluk side by side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = taluk,
                onValueChange = { taluk = it },
                label = { Text("Taluk") },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true
            )
        }

        // Address field
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 24.dp),
            maxLines = 4
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button
            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .padding(end = 8.dp)
            ) {
                Text("Back")
            }

            // Sign Up button
            Button(
                onClick = {
                    onSignupComplete()
                    // Create user data map
                    val userData = mapOf(
                        "username" to username,
                        "userType" to selectedUserType,
                        "state" to state,
                        "district" to district,
                        "taluk" to taluk,
                        "address" to address,
                        "email" to email,
                        "phoneNumber" to phoneNumber
                    )

                    coroutineScope.launch {
                        if (useEmailAuth) {
                            // Validate email fields
                            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            if (password != confirmPassword) {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            authViewModel.registerWithEmail(email, password, userData)
                        } else {
                            // Validate phone fields
                            if (username.isBlank() || phoneNumber.isBlank() || otp.isBlank()) {
                                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                                return@launch
                            }

                            authViewModel.verifyPhoneCode(otp, userData)
                        }
                    }
                },
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .padding(start = 8.dp),
                enabled = !isLoading && (
                        (useEmailAuth && username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password == confirmPassword) ||
                                (!useEmailAuth && username.isNotBlank() && phoneNumber.isNotBlank() && otp.isNotBlank() && otpSent)
                        )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Sign Up")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    SignupScreen()
}