package com.zerohunger.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.ui.theme.DarkGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
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
                .padding(24.dp)
        ) {
            Text(
                "Need Help?",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DarkGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Voice Instructions", fontWeight = FontWeight.Bold)
            Text("We are working on integrating Text-to-Speech support for local languages. This will be available in the next update.", color = androidx.compose.ui.graphics.Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Emergency Contact", fontWeight = FontWeight.Bold)
            Text("If you need immediate assistance regarding a food request, please call the local community center at: +1-800-ZEROHUNGER", color = androidx.compose.ui.graphics.Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Food Safety Guidelines", fontWeight = FontWeight.Bold)
            Text("• Only donate food that is safe for consumption.\n• Ensure packages are sealed and labeled if possible.\n• Check expiration dates carefully.", color = androidx.compose.ui.graphics.Color.Gray)
        }
    }
}
