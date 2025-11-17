package com.example.bankingapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bankingapp.ui.auth.login.LoginScreen
import com.example.bankingapp.ui.auth.register.RegisterScreen
import com.example.bankingapp.ui.auth.reset.ResetPasswordScreen
import com.example.bankingapp.ui.home.HomeScreen
import com.example.bankingapp.ui.splash.SplashScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: String = Routes.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash
        composable(Routes.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login
        composable(Routes.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                },
                onNavigateToResetPassword = {
                    navController.navigate(Routes.ResetPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register
        composable(Routes.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Reset Password
        composable(Routes.ResetPassword.route) {
            ResetPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Home
        composable(Routes.Home.route) {
            HomeScreen(
                onNavigateToTransactions = {
                    navController.navigate(Routes.TransactionsList.route)
                },
                onNavigateToLoans = {
                    navController.navigate(Routes.LoanSimulator.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.Notifications.route)
                },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}