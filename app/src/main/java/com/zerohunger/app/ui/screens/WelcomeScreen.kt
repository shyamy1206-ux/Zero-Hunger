package com.zerohunger.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.speech.tts.TextToSpeech

import com.zerohunger.app.R
import java.util.Locale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.ChevronRight
@Composable
fun WelcomeScreen(
    onOrganizationLogin: () -> Unit,
    onDonateFood: () -> Unit,
    onRequestFood: () -> Unit,
    onMapClick: () -> Unit
) {
    var logoTaps by remember { mutableStateOf(0) }
    var showKeywordDialog by remember { mutableStateOf(false) }
    var keywordInput by remember { mutableStateOf("") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Text-To-Speech Engine for Accessibility
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    
    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US // Ready for Hindi/Marathi later
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    // Secret tap logic to open Admin Login
    LaunchedEffect(logoTaps) {
        if (logoTaps >= 5) {
            logoTaps = 0
            showKeywordDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hidden trigger on the Logo text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null 
                ) {
                    logoTaps++
                }
            ) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFF2E7D32)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Zero Hunger",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32),
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // TTS Language Bar for Accessibility
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Voice Guide", tint = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.width(8.dp))
                
                TextButton(onClick = {
                    com.zerohunger.app.utils.LanguageManager.currentLanguage.value = com.zerohunger.app.utils.Language.ENGLISH
                    tts?.language = Locale("en", "US")
                    tts?.speak("Welcome to Zero Hunger. Press the first button to donate food. Press the second button to request food. Press the third button for the live map.", TextToSpeech.QUEUE_FLUSH, null, null)
                }) {
                    Text("English", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                
                Text("|", color = Color(0xFF2E7D32))
                
                TextButton(onClick = {
                    com.zerohunger.app.utils.LanguageManager.currentLanguage.value = com.zerohunger.app.utils.Language.HINDI
                    val loc = Locale("hi", "IN")
                    if (tts?.setLanguage(loc) == TextToSpeech.LANG_MISSING_DATA || tts?.setLanguage(loc) == TextToSpeech.LANG_NOT_SUPPORTED) {
                        android.widget.Toast.makeText(context, "Hindi TTS not supported", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        tts?.speak("जीरो हंगर में आपका स्वागत है। खाना दान करने के लिए पहला बटन दबाएं। भोजन का अनुरोध करने के लिए दूसरा बटन दबाएं। लाइव मैप देखने के लिए तीसरा बटन दबाएं।", TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }) {
                    Text("हिन्दी", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
                
                Text("|", color = Color(0xFF2E7D32))
                
                TextButton(onClick = {
                    com.zerohunger.app.utils.LanguageManager.currentLanguage.value = com.zerohunger.app.utils.Language.MARATHI
                    val loc = Locale("mr", "IN")
                    if (tts?.setLanguage(loc) == TextToSpeech.LANG_MISSING_DATA || tts?.setLanguage(loc) == TextToSpeech.LANG_NOT_SUPPORTED) {
                        android.widget.Toast.makeText(context, "Marathi TTS not supported", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        tts?.speak("झिरो हंगर मध्ये आपले स्वागत आहे. अन्न दान करण्यासाठी पहिले बटण दाबा. अन्नाची विनंती करण्यासाठी दुसरे बटण दाबा. थेट नकाशा पाहण्यासाठी तिसरे बटण दाबा.", TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }) {
                    Text("मराठी", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 3D Donate Button with Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable {
                        tts?.speak("Opening Donate Food Screen", TextToSpeech.QUEUE_FLUSH, null, null)
                        onDonateFood()
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.donate_food_icon_realistic_1789069961152),
                        contentDescription = "Donate Food",
                        modifier = Modifier
                            .size(116.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = com.zerohunger.app.utils.AppStrings.get(com.zerohunger.app.utils.AppStrings.donateFood), 
                            fontSize = 24.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = com.zerohunger.app.utils.AppStrings.get(com.zerohunger.app.utils.AppStrings.donateDesc), 
                            fontSize = 14.sp, 
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // 3D Request Button with Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clickable {
                        tts?.speak("Opening Request Food Screen", TextToSpeech.QUEUE_FLUSH, null, null)
                        onRequestFood()
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.request_food_icon_realistic_1789069972105),
                        contentDescription = "Request Food",
                        modifier = Modifier
                            .size(116.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = com.zerohunger.app.utils.AppStrings.get(com.zerohunger.app.utils.AppStrings.requestFood), 
                            fontSize = 24.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = com.zerohunger.app.utils.AppStrings.get(com.zerohunger.app.utils.AppStrings.requestDesc), 
                            fontSize = 14.sp, 
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 3D Location Map Button with Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clickable {
                        tts?.speak("Opening Live Donation Map", TextToSpeech.QUEUE_FLUSH, null, null)
                        onMapClick()
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.location_pin_icon_realistic_1789069982000),
                        contentDescription = "Live Map",
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = com.zerohunger.app.utils.AppStrings.get(com.zerohunger.app.utils.AppStrings.liveMap), 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = com.zerohunger.app.utils.AppStrings.get(com.zerohunger.app.utils.AppStrings.mapDesc), 
                            fontSize = 13.sp, 
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Join your community in reducing food waste.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        
        if (showKeywordDialog) {
            AlertDialog(
                onDismissRequest = { showKeywordDialog = false; keywordInput = "" },
                title = { Text("Enter Admin Keyword") },
                text = {
                    OutlinedTextField(
                        value = keywordInput,
                        onValueChange = { keywordInput = it },
                        singleLine = true,
                        label = { Text("Keyword") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (keywordInput == "Vrush@bh") {
                                showKeywordDialog = false
                                keywordInput = ""
                                onOrganizationLogin()
                            } else {
                                android.widget.Toast.makeText(context, "Invalid Keyword", android.widget.Toast.LENGTH_SHORT).show()
                                showKeywordDialog = false
                                keywordInput = ""
                            }
                        }
                    ) {
                        Text("Continue")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showKeywordDialog = false; keywordInput = "" }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
