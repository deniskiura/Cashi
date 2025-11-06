package ke.kiura.cashi.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionHistoryUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TransactionHistoryViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            getTransactionsUseCase().collect { result ->
                when (result) {
                    is DomainState.Success -> {
                        _uiState.value = _uiState.value.copy(
                            transactions = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is DomainState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is DomainState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun retry() {
        loadTransactions()
    }
}
