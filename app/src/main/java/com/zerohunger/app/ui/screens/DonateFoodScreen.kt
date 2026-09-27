package com.zerohunger.app.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.data.FoodItem
import com.zerohunger.app.utils.LocationHelper
import com.zerohunger.app.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateFoodScreen(
    viewModel: CommunityViewModel,
    onBack: () -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var servings by remember { mutableStateOf("") }
    var donorName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var pickupOrDelivery by remember { mutableStateOf("Pickup") }
    var daysToExpiry by remember { mutableStateOf("1") }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isFetchingLocation by remember { mutableStateOf(false) }

    var currentLat by remember { mutableStateOf<Double?>(null) }
    var currentLng by remember { mutableStateOf<Double?>(null) }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isFetchingLocation = true
            coroutineScope.launch {
                val locData = LocationHelper.getCurrentLocationData(context)
                if (locData != null) {
                    location = locData.address
                    currentLat = locData.latitude
                    currentLng = locData.longitude
                } else {
                    Toast.makeText(context, "Please enable GPS or type address manually", Toast.LENGTH_LONG).show()
                }
                isFetchingLocation = false
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donate Food") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = { Text("Your Name or Organization") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Food Name (e.g. Rice, Bread)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500), initialOffsetY = { 50 })
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit (kg, liters, items)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = servings,
                    onValueChange = { servings = it },
                    label = { Text("How many people can this serve?") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(700)) + slideInVertically(tween(700), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = daysToExpiry,
                    onValueChange = { daysToExpiry = it },
                    label = { Text("Days until expiry (Safe to eat)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (Address or Landmark)") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { 
                            locationPermissionLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ))
                        }) {
                            if (isFetchingLocation) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = "Use Current Location")
                            }
                        }
                    }
                )
            }
            
            var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                imageUri = uri
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(900)) + slideInVertically(tween(900), initialOffsetY = { 50 })
            ) {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (imageUri != null) "Photo Selected (Tap to change)" else "Attach a Photo")
                }
            }
            
            var isSubmitting by remember { mutableStateOf(false) }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1000)) + slideInVertically(tween(1000), initialOffsetY = { 50 })
            ) {
                Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: 0
                    val srv = servings.toIntOrNull() ?: 0
                    val days = daysToExpiry.toLongOrNull() ?: 1L
                    val expiryMillis = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
                    
                    if (foodName.isNotBlank() && qty > 0) {
                        isSubmitting = true
                        var imageBytes: ByteArray? = null
                        if (imageUri != null) {
                            context.contentResolver.openInputStream(imageUri!!)?.use {
                                imageBytes = it.readBytes()
                            }
                        }

                        coroutineScope.launch {
                            viewModel.donateFood(
                                FoodItem(
                                    name = foodName,
                                    quantity = qty,
                                    unit = unit,
                                    servings = srv,
                                    expiryTime = expiryMillis,
                                    preparedTime = System.currentTimeMillis(),
                                    status = "AVAILABLE",
                                    donorName = donorName.ifBlank { "Anonymous" },
                                    pickupOrDelivery = pickupOrDelivery,
                                    location = location.ifBlank { "Unknown" },
                                    latitude = currentLat,
                                    longitude = currentLng,
                                    // New V3 fields defaults
                                    vegetarian = true,
                                    allergens = "None",
                                    storageCondition = "Room Temperature",
                                    packed = true,
                                    adminSafetyApproval = false,
                                    isSynced = false
                                ),
                                imageBytes
                            )
                            isSubmitting = false
                            Toast.makeText(context, "Donation Submitted!", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    } else {
                        Toast.makeText(context, "Please enter a valid Food Name and Quantity", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submitting...", fontSize = 18.sp)
                } else {
                    Text("Submit Donation", fontSize = 18.sp)
                }
            }
            }
        }
    }
}
