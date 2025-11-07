package ke.kiura.cashi

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ke.kiura.cashi.presentation.history.TransactionHistoryScreen
import ke.kiura.cashi.presentation.sending.SendPaymentScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


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