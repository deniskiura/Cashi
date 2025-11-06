package ke.kiura.cashi.presentation.sending

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ke.kiura.cashi.R
import ke.kiura.cashi.domain.model.Currency
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendPaymentScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: SendPaymentViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showCurrencyDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Payment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
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
                placeholder = { Text("Email address") },
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

            // Error Message Card (Red)
            if (uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearMessages() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close_small),
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Success Message Card (Green)
            if (uiState.successMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.successMessage!!,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearMessages() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close_small),
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
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
        }
    }
}
