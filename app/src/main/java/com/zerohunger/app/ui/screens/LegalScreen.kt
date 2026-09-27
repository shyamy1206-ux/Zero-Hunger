package com.zerohunger.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.ui.theme.NavyDark
import com.zerohunger.app.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    title: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = OffWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OffWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        },
        containerColor = NavyDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Last Updated: September 2026",
                color = OffWhite.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("1. Information We Collect", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("We only collect necessary information required to facilitate food donations and requests. Phone numbers are securely stored and location data is not tracked in the background.", color = OffWhite.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(16.dp))

            Text("2. Data Security", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("All network communication is encrypted via HTTPS. Authentication is handled securely, and user roles are strictly enforced via our backend policies.", color = OffWhite.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(16.dp))

            Text("3. Offline Data", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("We may store temporary data on your device for offline functionality. This data is synced securely once an internet connection is re-established.", color = OffWhite.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("By continuing to use Zero Hunger Food Bank, you agree to these foundational privacy and security principles.", color = OffWhite, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}
