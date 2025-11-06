package ke.kiura.cashi.presentation.sending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.usecase.SendPaymentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SendPaymentUiState(
    val recipientEmail: String = "",
    val amount: String = "",
    val selectedCurrency: Currency = Currency.USD,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class SendPaymentViewModel(
    private val sendPaymentUseCase: SendPaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SendPaymentUiState())
    val uiState: StateFlow<SendPaymentUiState> = _uiState.asStateFlow()

    fun onRecipientEmailChanged(email: String) {
        _uiState.update { it.copy(recipientEmail = email, errorMessage = null) }
    }

    fun onAmountChanged(amount: String) {
        // Only allow valid decimal input
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
            _uiState.update { it.copy(amount = amount, errorMessage = null) }
        }
    }

    fun onCurrencySelected(currency: Currency) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }

    fun sendPayment() {
        val state = _uiState.value

        // Convert amount string to cents (Int)
        val amountInCents = try {
            val amountDouble = state.amount.toDoubleOrNull() ?: 0.0
            (amountDouble * 100).toInt()
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Invalid amount") }
            return
        }

        val payment = Payment(
            recipientEmail = state.recipientEmail.trim(),
            amount = amountInCents,
            currency = state.selectedCurrency
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            when (val result = sendPaymentUseCase(payment)) {
                is DomainState.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Payment sent successfully!",
                            recipientEmail = "",
                            amount = ""
                        )
                    }
                }
                is DomainState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is DomainState.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}
