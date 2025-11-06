package ke.kiura.cashi.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ke.kiura.cashi.R
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onNavigateToSendPayment: () -> Unit
) {
    val viewModel: TransactionHistoryViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cashi") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSendPayment
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send_money),
                    contentDescription = "Send Payment"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is TransactionHistoryViewModel.TransactionHistoryUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is TransactionHistoryViewModel.TransactionHistoryUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }

                is TransactionHistoryViewModel.TransactionHistoryUiState.Success -> {
                    if (state.transactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No transactions yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Send your first payment to see it here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.transactions) { transaction ->
                                TransactionItem(transaction = transaction)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Recipient and Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "To: ${transaction.recipient}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                StatusChip(status = transaction.status)
            }

            // Amount Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatAmount(transaction.amount, transaction.currency),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Timestamp
            Text(
                text = transaction.timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Transaction ID
            Text(
                text = "ID: ${transaction.id.take(8)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusChip(status: TransactionStatus) {
    val containerColor = when (status) {
        TransactionStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
        TransactionStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer
        TransactionStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (status) {
        TransactionStatus.COMPLETED -> MaterialTheme.colorScheme.onPrimaryContainer
        TransactionStatus.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
        TransactionStatus.FAILED -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

fun formatAmount(amountInCents: Int, currency: Currency): String {
    val amountInDollars = amountInCents / 100.0
    return "${currency.symbol}${String.format("%.2f", amountInDollars)} ${currency.code}"
}
