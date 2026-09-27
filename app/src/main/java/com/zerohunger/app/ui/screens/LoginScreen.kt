package com.zerohunger.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.viewmodel.AuthState
import com.zerohunger.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignupMode by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    fun isPasswordValid(pwd: String): Boolean {
        if (pwd.length < 8) return false
        if (!pwd.any { it.isDigit() }) return false
        if (!pwd.any { it.isLetter() }) return false
        if (!pwd.any { !it.isLetterOrDigit() }) return false
        return true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSignupMode) "Create Account" else "Secure Login") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Authorized Personnel Only",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    authViewModel.resetState()
                },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    authViewModel.resetState()
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            if (isSignupMode && password.isNotEmpty() && !isPasswordValid(password)) {
                Text(
                    text = "Password must be 8+ characters, with at least 1 number, 1 letter, and 1 symbol.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                )
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (isSignupMode) {
                        if (isPasswordValid(password)) {
                            authViewModel.signup(email, password)
                        }
                    } else {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotEmpty()
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (isSignupMode) "Sign Up" else "Login", fontSize = 18.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!isSignupMode) {
                TextButton(onClick = {
                    if (email.isNotBlank()) {
                        authViewModel.resetPassword(email) { success, msg ->
                            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                        }
                    } else {
                        android.widget.Toast.makeText(context, "Please enter your email first to reset password", android.widget.Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("Forgot Password?")
                }
            }

            TextButton(onClick = { 
                isSignupMode = !isSignupMode
                authViewModel.resetState()
            }) {
                Text(if (isSignupMode) "Already have an account? Login" else "New here? Sign Up")
            }
        }
    }
}
