package com.example.bankingapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bankingapp.ui.auth.login.LoginScreen
import com.example.bankingapp.ui.auth.register.RegisterScreen
import com.example.bankingapp.ui.auth.reset.ResetPasswordScreen
import com.example.bankingapp.ui.home.HomeScreen
import com.example.bankingapp.ui.loans.detail.LoanDetailScreen
import com.example.bankingapp.ui.loans.history.LoanHistoryScreen
import com.example.bankingapp.ui.loans.request.LoanRequestScreen
import com.example.bankingapp.ui.loans.simulator.LoanSimulatorScreen
import com.example.bankingapp.ui.notifications.NotificationsScreen
import com.example.bankingapp.ui.splash.SplashScreen
import com.example.bankingapp.ui.transactions.detail.TransactionDetailScreen
import com.example.bankingapp.ui.transactions.list.TransactionsListScreen

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

        // Transactions List
        composable(Routes.TransactionsList.route) {
            TransactionsListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTransactionClick = { transactionId ->
                    navController.navigate(Routes.TransactionDetail.createRoute(transactionId))
                }
            )
        }

        // Transaction Detail
        composable(
            route = Routes.TransactionDetail.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            TransactionDetailScreen(
                transactionId = transactionId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Loan Simulator
        composable(Routes.LoanSimulator.route) {
            LoanSimulatorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToRequest = { amount, rate, term ->
                    navController.navigate(Routes.LoanRequest.createRoute(amount, rate, term))
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.LoanHistory.route)
                }
            )
        }

        // Loan Request
        composable(
            route = Routes.LoanRequest.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.FloatType },
                navArgument("rate") { type = NavType.FloatType },
                navArgument("term") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
            val rate = backStackEntry.arguments?.getFloat("rate")?.toDouble() ?: 0.0
            val term = backStackEntry.arguments?.getInt("term") ?: 12

            LoanRequestScreen(
                amount = amount,
                rate = rate,
                termMonths = term,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRequestSuccess = {
                    navController.navigate(Routes.LoanHistory.route) {
                        popUpTo(Routes.LoanSimulator.route) { inclusive = false }
                    }
                }
            )
        }

        // Loan History
        composable(Routes.LoanHistory.route) {
            LoanHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoanClick = { loanId ->
                    navController.navigate(Routes.LoanDetail.createRoute(loanId))
                }
            )
        }

        // Loan Detail
        composable(
            route = Routes.LoanDetail.route,
            arguments = listOf(
                navArgument("loanId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getString("loanId") ?: ""
            LoanDetailScreen(
                loanId = loanId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Notifications
        composable(Routes.Notifications.route) {
            NotificationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNotificationClick = { relatedLoanId ->
                    relatedLoanId?.let {
                        navController.navigate(Routes.LoanDetail.createRoute(it))
                    }
                }
            )
        }
    }
}