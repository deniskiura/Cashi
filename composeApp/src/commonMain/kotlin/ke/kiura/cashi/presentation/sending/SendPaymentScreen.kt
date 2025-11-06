package ke.kiura.cashi.presentation.sending

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ke.kiura.cashi.domain.model.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPaymentScreen(
    viewModel: SendPaymentViewModel,
    onNavigateToHistory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Payment") },
                actions = {
                    TextButton(onClick = onNavigateToHistory) {
                        Text("History")
                    }
                }
            )
        },
        snackbarHost = {
            if (uiState.successMessage != null || uiState.errorMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearMessages() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(uiState.successMessage ?: uiState.errorMessage ?: "")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Enter Payment Details",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Recipient Email Field
            OutlinedTextField(
                value = uiState.recipientEmail,
                onValueChange = { viewModel.onRecipientEmailChanged(it) },
                label = { Text("Recipient Email") },
                placeholder = { Text("recipient@example.com") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.errorMessage?.contains("email", ignoreCase = true) == true
            )

            // Amount Field
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { viewModel.onAmountChanged(it) },
                label = { Text("Amount") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = {
                    Text(
                        text = uiState.selectedCurrency.symbol,
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                isError = uiState.errorMessage?.contains("amount", ignoreCase = true) == true
            )

            // Currency Dropdown
            ExposedDropdownMenuBox(
                expanded = showCurrencyDropdown,
                onExpandedChange = { showCurrencyDropdown = !uiState.isLoading && it }
            ) {
                OutlinedTextField(
                    value = "${uiState.selectedCurrency.name} (${uiState.selectedCurrency.symbol})",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Currency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCurrencyDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = !uiState.isLoading
                )

                ExposedDropdownMenu(
                    expanded = showCurrencyDropdown,
                    onDismissRequest = { showCurrencyDropdown = false }
                ) {
                    Currency.entries.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text("${currency.name} (${currency.symbol})") },
                            onClick = {
                                viewModel.onCurrencySelected(currency)
                                showCurrencyDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Send Payment Button
            Button(
                onClick = { viewModel.sendPayment() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading &&
                        uiState.recipientEmail.isNotBlank() &&
                        uiState.amount.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send Payment")
                }
            }

            // Error Message
            if (uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Success Message
            if (uiState.successMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.successMessage!!,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
