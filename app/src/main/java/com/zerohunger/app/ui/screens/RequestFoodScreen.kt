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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.data.BeneficiaryQueue
import com.zerohunger.app.utils.LocationHelper
import com.zerohunger.app.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestFoodScreen(
    viewModel: CommunityViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var peopleCount by remember { mutableStateOf("") }
    var requestedQuantity by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("NORMAL") }
    var location by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isFetchingLocation by remember { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }
    var tts: android.speech.tts.TextToSpeech? by remember { mutableStateOf(null) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    DisposableEffect(context) {
        val textToSpeech = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) { }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    fun speakText(lang: String, country: String, text: String) {
        tts?.let {
            val locale = java.util.Locale(lang, country)
            val result = it.setLanguage(locale)
            if (result == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || result == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT).show()
            } else {
                it.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    val helpEn = "Please fill this form to request food. Enter your name, number of people, and required quantity. Select urgency and provide your location."
    val helpHi = "कृपया भोजन का अनुरोध करने के लिए यह फॉर्म भरें। अपना नाम, लोगों की संख्या और आवश्यक मात्रा दर्ज करें। तात्कालिकता चुनें और अपना स्थान प्रदान करें।"
    val helpMr = "कृपया अन्नाची विनंती करण्यासाठी हा फॉर्म भरा. तुमचे नाव, लोकांची संख्या आणि आवश्यक प्रमाण प्रविष्ट करा. निकड निवडा आणि तुमचे स्थान प्रदान करा."

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isFetchingLocation = true
            coroutineScope.launch {
                val address = LocationHelper.getCurrentAddress(context)
                if (address != null) {
                    location = address
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
                title = { Text("Request Food") },
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
            // TTS Language Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Voice Guide", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { speakText("en", "US", helpEn) }) {
                    Text("English", fontWeight = FontWeight.Bold)
                }
                Text("|")
                TextButton(onClick = { speakText("hi", "IN", helpHi) }) {
                    Text("हिन्दी", fontWeight = FontWeight.Bold)
                }
                Text("|")
                TextButton(onClick = { speakText("mr", "IN", helpMr) }) {
                    Text("मराठी", fontWeight = FontWeight.Bold)
                }
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Individual or Group Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400), initialOffsetY = { 50 })
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = peopleCount,
                        onValueChange = { peopleCount = it },
                        label = { Text("Number of People") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = requestedQuantity,
                        onValueChange = { requestedQuantity = it },
                        label = { Text("Req. Qty (e.g. 10 kg)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Contact Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600), initialOffsetY = { 50 })
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Delivery Location / Address") },
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
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(700)) + slideInVertically(tween(700), initialOffsetY = { 50 })
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { urgency = "NORMAL" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (urgency == "NORMAL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (urgency == "NORMAL") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Normal Request")
                    }
                    
                    Button(
                        onClick = { urgency = "URGENT" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (urgency == "URGENT") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (urgency == "URGENT") MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Urgent Needs")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var isSubmitting by remember { mutableStateOf(false) }
            var generatedOtp by remember { mutableStateOf<String?>(null) }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800), initialOffsetY = { 50 })
            ) {
                Button(
                    onClick = {
                        val count = peopleCount.toIntOrNull() ?: 1
                        val reqQty = requestedQuantity.toIntOrNull() ?: 0
                        
                        if (name.isNotBlank() && reqQty > 0) {
                            isSubmitting = true
                            val newOtp = (1000..9999).random().toString()
                            coroutineScope.launch {
                                viewModel.requestFood(
                                    BeneficiaryQueue(
                                        name = name,
                                        peopleCount = count,
                                        requestedQuantity = reqQty,
                                        urgency = urgency,
                                        location = location.ifBlank { "Unknown" },
                                        phoneNumber = phoneNumber,
                                        status = "WAITING",
                                        timestamp = System.currentTimeMillis(),
                                        urgent = urgency == "URGENT",
                                        pickupDate = null,
                                        pickupTime = "",
                                        otp = newOtp,
                                        isSynced = false
                                    )
                                )
                                isSubmitting = false
                                generatedOtp = newOtp
                            }
                        } else {
                            Toast.makeText(context, "Please enter a valid Name and Requested Quantity", Toast.LENGTH_LONG).show()
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
                        Text("Submit Request", fontSize = 18.sp)
                    }
                }
            }
            
            if (generatedOtp != null) {
                AlertDialog(
                    onDismissRequest = { 
                        generatedOtp = null
                        onBack()
                    },
                    title = { Text("Request Submitted!") },
                    text = { 
                        Column {
                            Text("Your food request has been added to the queue.")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Your Pickup OTP is:", fontWeight = FontWeight.Bold)
                            Text(generatedOtp!!, fontSize = 32.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Please show this code to the admin when collecting your food.")
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            generatedOtp = null
                            onBack()
                        }) {
                            Text("Got it")
                        }
                    }
                )
            }
        }
    }
}
