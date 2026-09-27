package com.zerohunger.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.data.ActionHistory
import com.zerohunger.app.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.zerohunger.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val history by viewModel.actionHistory.collectAsState()
    var showUndoConfirm by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audit Logs & Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Export Menu
                    if (history.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export Report"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Export as CSV") },
                                onClick = { 
                                    expanded = false
                                    viewModel.exportCsvReport(context) 
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as PDF") },
                                onClick = { 
                                    expanded = false
                                    viewModel.exportPdfReport(context) 
                                }
                            )
                        }
                    }
                    if (history.any { it.canUndo }) {
                        IconButton(onClick = { showUndoConfirm = true }) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo Last Entry")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDark,
                    titleContentColor = OffWhite,
                    navigationIconContentColor = OffWhite,
                    actionIconContentColor = OffWhite
                )
            )
        },
        containerColor = NavyDark
    ) { paddingValues ->
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No actions recorded yet.", color = OffWhite, fontSize = 20.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary Cards
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(
                            modifier = Modifier.weight(1f).height(100.dp),
                            colors = CardDefaults.cardColors(containerColor = NavySurface)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("${history.count { it.actionType == "DONATION" }}", fontSize = 28.sp, color = DarkGreen, fontWeight = FontWeight.Bold)
                                Text("Donations", fontSize = 14.sp, color = LightGray)
                            }
                        }
                        
                        Card(
                            modifier = Modifier.weight(1f).height(100.dp),
                            colors = CardDefaults.cardColors(containerColor = NavySurface)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("${history.count { it.actionType == "DISTRIBUTION" }}", fontSize = 28.sp, color = WarningOrangeLight, fontWeight = FontWeight.Bold)
                                Text("Distributed", fontSize = 14.sp, color = LightGray)
                            }
                        }
                    }
                }
                
                item { Divider(color = NavySurfaceVariant, modifier = Modifier.padding(vertical = 8.dp)) }

                items(history) { action ->
                    HistoryCard(action)
                }
            }
        }
    }

    if (showUndoConfirm) {
        AlertDialog(
            onDismissRequest = { showUndoConfirm = false },
            title = { Text("Undo Last Entry") },
            text = { Text("Are you sure you want to undo the last valid action?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.undoLastAction()
                    showUndoConfirm = false
                }) {
                    Text("Undo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUndoConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryCard(action: ActionHistory) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(action.timestamp))
    
    val (iconColor, actionLabel) = when (action.actionType) {
        "DONATION" -> Pair(DarkGreen, "Food Donated")
        "REQUEST" -> Pair(WarningOrangeLight, "Food Requested")
        "DISTRIBUTION" -> Pair(com.zerohunger.app.ui.theme.ErrorRedLight, "Food Distributed")
        else -> Pair(LightGray, action.actionType)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(iconColor, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionLabel,
                        fontWeight = FontWeight.Bold,
                        color = iconColor,
                        fontSize = 14.sp
                    )
                    Text(
                        text = dateString,
                        color = OffWhite.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = action.description,
                    color = OffWhite.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
