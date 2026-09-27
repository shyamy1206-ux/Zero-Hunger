package com.zerohunger.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.ui.theme.OffWhite
import com.zerohunger.app.ui.theme.DarkGreen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHomeScreen(
    onDonate: () -> Unit,
    onRequest: () -> Unit,
    onFindNearby: () -> Unit,
    onTrackRequest: () -> Unit,
    onGetHelp: () -> Unit,
    onProfile: () -> Unit,
    onBack: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS initialized
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    fun speakText(lang: String, country: String, text: String) {
        tts?.let {
            val locale = Locale(lang, country)
            val result = it.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                android.widget.Toast.makeText(context, "Language not supported on this device", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                it.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    val englishText = "How can we help today? Button 1: Donate Food. Button 2: Request Food. Button 3: Find Nearby Food. Button 4: Track My Request. Button 5: Get Help."
    val hindiText = "हम आज आपकी कैसे मदद कर सकते हैं? बटन 1: खाना दान करें। बटन 2: खाने का अनुरोध करें। बटन 3: पास में खाना खोजें। बटन 4: अपने अनुरोध को ट्रैक करें। बटन 5: सहायता प्राप्त करें।"
    val marathiText = "आम्ही आज तुमची कशी मदत करू शकतो? बटण 1: अन्न दान करा. बटण 2: अन्नाची विनंती करा. बटण 3: जवळचे अन्न शोधा. बटण 4: आपल्या विनंतीचा मागोवा घ्या. बटण 5: मदत मिळवा."

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OffWhite,
                    titleContentColor = DarkGreen,
                    navigationIconContentColor = DarkGreen,
                    actionIconContentColor = DarkGreen
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OffWhite)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // TTS Language Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Voice Guide", tint = DarkGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { speakText("en", "US", englishText) }) {
                            Text("English", fontWeight = FontWeight.Bold, color = DarkGreen)
                        }
                        Text("|", color = DarkGreen)
                        TextButton(onClick = { speakText("hi", "IN", hindiText) }) {
                            Text("हिन्दी", fontWeight = FontWeight.Bold, color = DarkGreen)
                        }
                        Text("|", color = DarkGreen)
                        TextButton(onClick = { speakText("mr", "IN", marathiText) }) {
                            Text("मराठी", fontWeight = FontWeight.Bold, color = DarkGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(500)) + slideInVertically(tween(500), initialOffsetY = { -50 })
                    ) {
                        Text(
                            text = "How can we help today?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkGreen,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    AnimatedActionCard(
                        isVisible = isVisible,
                        delay = 600,
                        title = "Donate Food",
                        subtitle = "Share surplus food with others",
                        icon = Icons.Default.VolunteerActivism,
                        containerColor = DarkGreen,
                        contentColor = Color.White,
                        onClick = onDonate
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    AnimatedActionCard(
                        isVisible = isVisible,
                        delay = 700,
                        title = "Request Food",
                        subtitle = "Get help for yourself or your family",
                        icon = Icons.Default.LocalDining,
                        containerColor = Color.White,
                        contentColor = DarkGreen,
                        onClick = onRequest
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    AnimatedActionCard(
                        isVisible = isVisible,
                        delay = 800,
                        title = "Find Nearby Food",
                        subtitle = "View active donations on the map",
                        icon = Icons.Default.Map,
                        containerColor = Color.White,
                        contentColor = DarkGreen,
                        onClick = onFindNearby
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    AnimatedActionCard(
                        isVisible = isVisible,
                        delay = 900,
                        title = "Track My Request",
                        subtitle = "View your live pickup status",
                        icon = Icons.Default.ConfirmationNumber,
                        containerColor = Color.White,
                        contentColor = DarkGreen,
                        onClick = onTrackRequest
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    AnimatedActionCard(
                        isVisible = isVisible,
                        delay = 1000,
                        title = "Get Help",
                        subtitle = "Food safety and emergency contact",
                        icon = Icons.Default.HelpOutline,
                        containerColor = Color.White,
                        contentColor = DarkGreen,
                        onClick = onGetHelp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun AnimatedActionCard(
    isVisible: Boolean,
    delay: Int,
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(delay)) + slideInVertically(tween(delay), initialOffsetY = { 50 })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = contentColor)
                    Text(subtitle, fontSize = 14.sp, color = contentColor.copy(alpha = 0.8f))
                }
            }
        }
    }
}
