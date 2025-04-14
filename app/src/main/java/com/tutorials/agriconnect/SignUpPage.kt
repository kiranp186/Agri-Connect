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
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

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
    onSignupComplete: () -> Unit = {}
) {
    // Authentication Type
    var authType by remember { mutableStateOf("Phone") } // "Phone" or "Email"

    val context = LocalContext.current

    // Firebase Auth instance - with try-catch to handle initialization errors
    val auth = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        Toast.makeText(context, "Firebase initialization error: ${e.message}", Toast.LENGTH_LONG).show()
        null
    }

    // State variables for form fields
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // User type selection
    var expanded by remember { mutableStateOf(false) }
    var selectedUserType by remember { mutableStateOf("Select User Type") }
    val userTypes = listOf("Farmer", "Equipment Provider")

    // Location details
    var state by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var taluk by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

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

        // Authentication Type Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    authType = "Phone"
                    otpSent = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (authType == "Phone")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Phone")
            }

            Button(
                onClick = {
                    authType = "Email"
                    otpSent = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (authType == "Email")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Email")
            }
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

        if (authType == "Phone") {
            // Phone number field with OTP button
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

// Inside your phone auth button click
                Button(
                    onClick = {
                        if (auth == null) {
                            Toast.makeText(context, "Firebase not initialized. Please restart the app.", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        try {
                            isLoading = true
                            // Format phone number with country code (assuming India)
                            val formattedPhoneNumber = "+91$phoneNumber"

                            // Simple callback for phone verification
                            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    isLoading = false
                                    Toast.makeText(context, "Verification completed", Toast.LENGTH_SHORT).show()
                                }

                                override fun onVerificationFailed(e: FirebaseException) {
                                    isLoading = false
                                    Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }

                                override fun onCodeSent(
                                    verId: String,
                                    token: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    verificationId = verId
                                    otpSent = true
                                    isLoading = false
                                    Toast.makeText(context, "OTP sent to your phone", Toast.LENGTH_SHORT).show()
                                }
                            }

                            // Use try-catch for all Firebase operations
                            try {
                                val options = PhoneAuthOptions.newBuilder(auth!!)
                                    .setPhoneNumber(formattedPhoneNumber)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(context as ComponentActivity)
                                    .setCallbacks(callbacks)
                                    .build()
                                PhoneAuthProvider.verifyPhoneNumber(options)
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Error sending OTP: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = phoneNumber.length == 10 && !isLoading,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Get OTP")
                    }
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
        } else {
            // Email field
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

            // Password field (only shown for email auth)
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

            // Confirm Password field
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

        // Location fields
        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("State") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

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
                    if (auth == null) {
                        Toast.makeText(context, "Firebase not initialized. Please restart the app.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    isLoading = true
                    if (authType == "Phone" && otpSent) {
                        // Verify OTP and complete sign up
                        try {
                            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        // Phone verification successful, continue with registration
                                        Toast.makeText(context, "OTP verified successfully!", Toast.LENGTH_SHORT).show()
                                        onSignupComplete()
                                    } else {
                                        // Phone verification failed
                                        Toast.makeText(context, "OTP verification failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } else if (authType == "Email") {
                        // Email and password sign up
                        if (email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        // Email registration successful
                                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                        onSignupComplete()
                                    } else {
                                        // Email registration failed
                                        Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            isLoading = false
                            Toast.makeText(context, "Please complete all fields correctly", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        isLoading = false
                        Toast.makeText(context, "Please complete authentication first", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading && ((authType == "Phone" && otpSent && otp.length == 6) ||
                        (authType == "Email" && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword)),
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
                    .padding(start = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
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