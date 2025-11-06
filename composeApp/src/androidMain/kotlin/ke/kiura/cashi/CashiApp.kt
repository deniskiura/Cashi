package ke.kiura.cashi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import ke.kiura.cashi.presentation.history.TransactionHistoryScreen
import ke.kiura.cashi.presentation.history.TransactionHistoryViewModel
import ke.kiura.cashi.presentation.sending.SendPaymentScreen
import ke.kiura.cashi.presentation.sending.SendPaymentViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel



@Composable
@Preview
fun CashiApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.TransactionHistory) }

    MaterialTheme {
        when (currentScreen) {
            Screen.SendPayment -> {
                SendPaymentScreen(
                    onNavigateBack = { currentScreen = Screen.TransactionHistory }
                )
            }
            Screen.TransactionHistory -> {
                TransactionHistoryScreen(
                    onNavigateToSendPayment = { currentScreen = Screen.SendPayment }
                )
            }
        }
    }
}

sealed class Screen {
    data object SendPayment : Screen()
    data object TransactionHistory : Screen()
}