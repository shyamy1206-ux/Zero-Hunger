package com.zerohunger.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerohunger.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: com.zerohunger.app.viewmodel.AdminViewModel,
    onNavigateToInventory: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLegal: () -> Unit,
    onBack: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = NavySurface,
                drawerContentColor = OffWhite
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Control Room",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkGreen,
                    modifier = Modifier.padding(16.dp)
                )
                Divider(color = NavySurfaceVariant)
                Spacer(Modifier.height(16.dp))
                
                NavigationDrawerItem(
                    label = { Text("Dashboard", color = OffWhite) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null, tint = OffWhite) },
                    colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = NavySurfaceVariant),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Food Inventory", color = OffWhite) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToInventory()
                    },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = null, tint = OffWhite) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Distribution Queue", color = OffWhite) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToQueue()
                    },
                    icon = { Icon(Icons.Default.People, contentDescription = null, tint = OffWhite) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Audit Logs & Reports", color = OffWhite) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToHistory()
                    },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = null, tint = OffWhite) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Notifications", color = OffWhite) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToNotifications()
                    },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = OffWhite) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Privacy Policy", color = OffWhite) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToLegal()
                    },
                    icon = { Icon(Icons.Default.Policy, contentDescription = null, tint = OffWhite) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.weight(1f))
                Divider(color = NavySurfaceVariant)
                NavigationDrawerItem(
                    label = { Text("Secure Logout", color = ErrorRedLight) },
                    selected = false,
                    onClick = onBack,
                    icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = ErrorRedLight) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Logistics Overview", fontWeight = FontWeight.Bold, color = OffWhite) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = OffWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyDark
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NavyDark)
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val availableFood by viewModel.availableFood.collectAsState()
                    val waitingQueue by viewModel.waitingQueue.collectAsState()
                    val syncError by viewModel.syncError.collectAsState()

                    if (syncError != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = ErrorRedLight)
                        ) {
                            Text(
                                "Sync Error: $syncError",
                                color = NavyDark,
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(600)) + slideInVertically(tween(600), initialOffsetY = { -30 })
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f).height(120.dp),
                                colors = CardDefaults.cardColors(containerColor = NavySurface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("${availableFood.size}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = DarkGreen)
                                    Text("Safe Items", fontSize = 14.sp, color = LightGray)
                                }
                            }
                            
                            Card(
                                modifier = Modifier.weight(1f).height(120.dp),
                                colors = CardDefaults.cardColors(containerColor = NavySurface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("${waitingQueue.size}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = WarningOrangeLight)
                                    Text("Pending", fontSize = 14.sp, color = LightGray)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(800)) + slideInVertically(tween(800), initialOffsetY = { 50 })
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToQueue() },
                            colors = CardDefaults.cardColors(containerColor = NavySurfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = WarningOrangeLight, modifier = Modifier.size(32.dp))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text("Action Required", color = OffWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("${waitingQueue.size} beneficiaries are waiting in the queue for food distribution.", color = LightGray, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
