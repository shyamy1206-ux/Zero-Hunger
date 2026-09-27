package com.zerohunger.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.data.FoodItem
import com.zerohunger.app.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

import com.zerohunger.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val availableFood by viewModel.availableFood.collectAsState()
    var searchQuery by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    
    // Sort by nearest expiry date first and filter by search
    val sortedFood = availableFood
        .filter { it.name.contains(searchQuery, ignoreCase = true) || it.donorName.contains(searchQuery, ignoreCase = true) }
        .sortedBy { it.expiryTime }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Inventory & Safety") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark, titleContentColor = OffWhite, navigationIconContentColor = OffWhite)
            )
        },
        containerColor = NavyDark
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name or donor...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true
            )

            if (sortedFood.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(if (availableFood.isEmpty()) "No food available right now." else "No matching items found.", fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                items(sortedFood) { item ->
                    FoodItemCard(
                        item = item,
                        onToggleSafety = {
                            val itemId = item.id
                            if (itemId != null) {
                                viewModel.toggleSafetyApproval(itemId, item.adminSafetyApproval)
                            }
                        }
                    )
                }
            }
        }
        }
    }
}

@Composable
fun FoodItemCard(item: FoodItem, onToggleSafety: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    val timeUntilExpiry = item.expiryTime - currentTime
    
    val isExpired = timeUntilExpiry <= 0
    val isExpiringSoon = timeUntilExpiry > 0 && timeUntilExpiry < 2 * 24 * 60 * 60 * 1000L // Less than 48 hrs
    
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    val cardColor = when {
        isExpired -> Color(0xFFFFEBEE) // Light Red
        isExpiringSoon -> Color(0xFFFFF3E0) // Light Orange
        else -> Color.White
    }

    val statusColor = when {
        isExpired -> Color(0xFFD32F2F)
        isExpiringSoon -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                if (isExpired) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Expired", tint = statusColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EXPIRED", color = statusColor, fontWeight = FontWeight.Bold)
                    }
                } else if (isExpiringSoon) {
                    Text("EXPIRING SOON", color = statusColor, fontWeight = FontWeight.Bold)
                } else {
                    Text("FRESH", color = statusColor, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(text = "Quantity: ${item.quantity} ${item.unit} (Serves: ${item.servings})", fontSize = 16.sp)
            Text(text = "Donor: ${item.donorName} • ${item.pickupOrDelivery}", fontSize = 16.sp)
            Text(
                text = "Expires: ${dateFormat.format(Date(item.expiryTime))}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            
            // V3 Details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Type: ${if(item.vegetarian) "🟩 Veg" else "🟥 Non-Veg"}", fontSize = 14.sp)
                Text("Storage: ${item.storageCondition}", fontSize = 14.sp)
            }
            Text("Allergens: ${item.allergens}", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Admin Safety Approval - Clickable Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (item.adminSafetyApproval) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (item.adminSafetyApproval) Color(0xFF388E3C) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (item.adminSafetyApproval) "Safety Approved" else "Pending Safety Check",
                        color = if (item.adminSafetyApproval) Color(0xFF388E3C) else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = onToggleSafety,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.adminSafetyApproval) Color(0xFFEF5350) else Color(0xFF388E3C)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (item.adminSafetyApproval) "Revoke" else "Approve",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
