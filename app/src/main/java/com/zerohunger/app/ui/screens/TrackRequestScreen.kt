package com.zerohunger.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.viewmodel.CommunityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackRequestScreen(
    viewModel: CommunityViewModel,
    onBack: () -> Unit
) {
    // In a real app, we'd filter by the current user's ID/Phone.
    // For this release, we'll just show the queue to track statuses.
    val queue by viewModel.myRequests.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track My Requests") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.zerohunger.app.ui.theme.OffWhite
                )
            )
        },
        containerColor = com.zerohunger.app.ui.theme.OffWhite
    ) { paddingValues ->
        if (queue.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("You have no active requests.", color = com.zerohunger.app.ui.theme.DarkText, fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(queue) { request ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Request Status",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Surface(
                                    color = when (request.requestStatus) {
                                        "COMPLETED" -> com.zerohunger.app.ui.theme.DarkGreen
                                        "READY" -> com.zerohunger.app.ui.theme.CalmGreen
                                        "RECEIVED" -> com.zerohunger.app.ui.theme.WarningOrangeLight
                                        else -> com.zerohunger.app.ui.theme.LightGray
                                    },
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = request.requestStatus,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = androidx.compose.ui.graphics.Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Quantity: ${request.requestedQuantity} servings")
                            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            Text("Requested on: ${sdf.format(Date(request.timestamp))}")
                            
                            if (request.otp.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Your Pickup Code: ${request.otp}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = com.zerohunger.app.ui.theme.DarkGreen,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
