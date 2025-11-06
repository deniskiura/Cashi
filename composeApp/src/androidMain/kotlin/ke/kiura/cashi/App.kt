package ke.kiura.cashi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import ke.kiura.cashi.presentation.history.TransactionHistoryScreen
import ke.kiura.cashi.presentation.history.TransactionHistoryViewModel
import ke.kiura.cashi.presentation.sending.SendPaymentScreen
import ke.kiura.cashi.presentation.sending.SendPaymentViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

sealed class Screen {
    data object SendPayment : Screen()
    data object TransactionHistory : Screen()
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        CashiApp()
    }
}

@Composable
fun CashiApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.SendPayment) }

    when (currentScreen) {
        Screen.SendPayment -> {
            val viewModel: SendPaymentViewModel = koinViewModel()
            SendPaymentScreen(
                viewModel = viewModel,
                onNavigateToHistory = { currentScreen = Screen.TransactionHistory }
            )
        }
        Screen.TransactionHistory -> {
            val viewModel: TransactionHistoryViewModel = koinViewModel()
            TransactionHistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { currentScreen = Screen.SendPayment }
            )
        }
    }
}