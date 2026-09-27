package com.zerohunger.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.data.BeneficiaryQueue
import com.zerohunger.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiaryQueueScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val waitingQueue by viewModel.waitingQueue.collectAsState()
    val availableFood by viewModel.availableFood.collectAsState()

    var showDistributeDialog by remember { mutableStateOf<BeneficiaryQueue?>(null) }
    var showOtpDialog by remember { mutableStateOf<Pair<BeneficiaryQueue, Pair<Long, Int>>?>(null) }
    var otpInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("People Waiting") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (waitingQueue.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No one is waiting right now.", fontSize = 20.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(waitingQueue) { index, person ->
                    BeneficiaryCard(
                        person = person,
                        isNext = index == 0,
                        onDistribute = { showDistributeDialog = person }
                    )
                }
            }
        }
    }

    // Step 1: Pick which food to distribute
    if (showDistributeDialog != null) {
        val person = showDistributeDialog!!
        
        // Phase 4: Only allow distribution of Safe & Non-Expired food
        val currentTime = System.currentTimeMillis()
        val distributableFood = availableFood.filter { 
            it.adminSafetyApproval && it.expiryTime > currentTime 
        }
        
        AlertDialog(
            onDismissRequest = { showDistributeDialog = null },
            title = { Text("Distribute Food to ${person.name}") },
            text = {
                if (distributableFood.isEmpty()) {
                    Text("No safe/fresh food is currently available for distribution. Please check inventory for expired items.")
                } else {
                    LazyColumn {
                        items(distributableFood) { food ->
                            Button(
                                onClick = {
                                    val bId = person.id
                                    val fId = food.id
                                    if (bId != null && fId != null) {
                                        val qty = if (food.quantity >= person.requestedQuantity) person.requestedQuantity else food.quantity
                                        showDistributeDialog = null
                                        // If OTP exists, require verification first
                                        if (person.otp.isNotBlank()) {
                                            showOtpDialog = Pair(person, Pair(fId, qty))
                                            otpInput = ""
                                        } else {
                                            viewModel.distributeFood(bId, fId, qty)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Give ${food.name} (Available: ${food.quantity})")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDistributeDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Step 2: OTP Verification Dialog
    if (showOtpDialog != null) {
        val (person, foodInfo) = showOtpDialog!!
        val (foodId, qty) = foodInfo

        AlertDialog(
            onDismissRequest = { showOtpDialog = null },
            title = { Text("Verify OTP") },
            text = {
                Column {
                    Text("Ask ${person.name} for their 4-digit pickup code:", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { if (it.length <= 4) otpInput = it },
                        label = { Text("Enter OTP") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (otpInput == person.otp) {
                            val bId = person.id
                            if (bId != null) {
                                viewModel.distributeFood(bId, foodId, qty)
                            }
                            showOtpDialog = null
                            otpInput = ""
                        } else {
                            // Wrong OTP - don't close
                            otpInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Verify & Distribute")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = null; otpInput = "" }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BeneficiaryCard(person: BeneficiaryQueue, isNext: Boolean, onDistribute: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isNext) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isNext) {
                Text("NEXT TO SERVE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(text = person.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Needs: ${person.requestedQuantity} for ${person.peopleCount} people", fontSize = 18.sp)
            Text(text = "Location: ${person.location}", fontSize = 18.sp)
            if (person.phoneNumber.isNotBlank()) {
                Text(text = "Phone: ${person.phoneNumber}", fontSize = 16.sp, color = Color.Gray)
            }
            if (person.urgency == "URGENT") {
                Text("URGENT", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
            if (person.otp.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pickup OTP: ${person.otp}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDistribute, modifier = Modifier.fillMaxWidth()) {
                Text("Distribute Food", fontSize = 18.sp)
            }
        }
    }
}
