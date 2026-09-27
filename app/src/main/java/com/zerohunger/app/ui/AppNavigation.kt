package com.zerohunger.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zerohunger.app.data.AppRepository
import com.zerohunger.app.ui.screens.*
import com.zerohunger.app.viewmodel.AdminViewModel
import com.zerohunger.app.viewmodel.AdminViewModelFactory
import com.zerohunger.app.viewmodel.CommunityViewModel
import com.zerohunger.app.viewmodel.CommunityViewModelFactory
import com.zerohunger.app.data.SupabaseClient
import com.zerohunger.app.viewmodel.AuthViewModel
import com.zerohunger.app.viewmodel.AuthViewModelFactory
import com.zerohunger.app.viewmodel.AuthState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.jan.supabase.gotrue.auth

@Composable
fun AppNavigation(repository: AppRepository) {
    val navController = rememberNavController()

    val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModelFactory(repository))
    val communityViewModel: CommunityViewModel = viewModel(factory = CommunityViewModelFactory(repository))
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory())

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(
                onOrganizationLogin = { 
                    if (authViewModel.authState.value is AuthState.Authenticated) {
                        navController.navigate("admin_dashboard")
                    } else {
                        navController.navigate("login/admin") 
                    }
                },
                onDonateFood = { 
                    navController.navigate("donate_food")
                },
                onRequestFood = { 
                    navController.navigate("request_food")
                },
                onMapClick = {
                    navController.navigate("map_screen")
                }
            )
        }
        
        composable("map_screen") {
            com.zerohunger.app.ui.screens.MapScreen(
                viewModel = communityViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("login/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "community"
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { 
                    val destination = when(type) {
                        "admin" -> "admin_dashboard"
                        "donate" -> "donate_food"
                        "request" -> "request_food"
                        else -> "donate_food"
                    }
                    navController.navigate(destination) {
                        popUpTo("welcome")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        // Admin Portal
        composable("admin_dashboard") {
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onNavigateToInventory = { navController.navigate("inventory") },
                onNavigateToQueue = { navController.navigate("queue") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToLegal = { navController.navigate("legal") },
                onBack = { 
                    authViewModel.logout()
                    navController.popBackStack() 
                }
            )
        }
        
        composable("notifications") {
            NotificationCenterScreen(
                viewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("legal") {
            LegalScreen(
                title = "Privacy Policy & Terms",
                onBack = { navController.popBackStack() }
            )
        }

        composable("inventory") {
            InventoryScreen(
                viewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("queue") {
            BeneficiaryQueueScreen(
                viewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            val authState by authViewModel.authState.collectAsState()
            val email = if (authState is AuthState.Authenticated) {
                try {
                    SupabaseClient.client.auth.currentSessionOrNull()?.user?.email ?: "Community User"
                } catch (e: Exception) { "Community User" }
            } else "Community User"
            ProfileScreen(
                userEmail = email,
                onBack = { navController.popBackStack() }
            )
        }

        // Community Portal
        composable("community_home") {
            CommunityHomeScreen(
                onDonate = { navController.navigate("donate_food") },
                onRequest = { navController.navigate("request_food") },
                onFindNearby = { navController.navigate("map_screen") },
                onTrackRequest = { navController.navigate("track_request") },
                onGetHelp = { navController.navigate("help") },
                onProfile = { navController.navigate("profile") },
                onBack = { 
                    authViewModel.logout()
                    navController.popBackStack() 
                }
            )
        }
        
        composable("donate_food") {
            DonateFoodScreen(
                viewModel = communityViewModel,
                onBack = { 
                    navController.popBackStack() 
                }
            )
        }
        
        composable("track_request") {
            TrackRequestScreen(
                viewModel = communityViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("help") {
            HelpScreen(
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("request_food") {
            RequestFoodScreen(
                viewModel = communityViewModel,
                onBack = { 
                    navController.popBackStack() 
                }
            )
        }
    }
}
