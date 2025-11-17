package com.example.bankingapp.core.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Login : Routes("login")
    object Register : Routes("register")
    object ResetPassword : Routes("reset_password")
    object Home : Routes("home")
    object TransactionsList : Routes("transactions_list")
    object TransactionDetail : Routes("transaction_detail/{transactionId}") {
        fun createRoute(transactionId: String) = "transaction_detail/$transactionId"
    }
    object LoanSimulator : Routes("loan_simulator")
    object LoanRequest : Routes("loan_request/{amount}/{rate}/{term}") {
        fun createRoute(amount: Double, rate: Double, term: Int) =
            "loan_request/$amount/$rate/$term"
    }
    object LoanHistory : Routes("loan_history")
    object LoanDetail : Routes("loan_detail/{loanId}") {
        fun createRoute(loanId: String) = "loan_detail/$loanId"
    }
    object Notifications : Routes("notifications")
}