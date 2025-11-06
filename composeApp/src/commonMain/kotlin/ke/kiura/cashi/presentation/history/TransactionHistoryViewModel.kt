package ke.kiura.cashi.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionHistoryUiState>(TransactionHistoryUiState.Loading)
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.onStart {
        loadTransactions()
    }.stateIn(
        scope = viewModelScope,
        initialValue = TransactionHistoryUiState.Loading,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun loadTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase().collect { result ->
                when (result) {
                    is DomainState.Loading -> _uiState.value = TransactionHistoryUiState.Loading
                    is DomainState.Success -> _uiState.value = TransactionHistoryUiState.Success(result.data)
                    is DomainState.Error -> _uiState.value = TransactionHistoryUiState.Error(result.message)
                }
            }
        }
    }

    fun retry() {
        loadTransactions()
    }

    sealed interface TransactionHistoryUiState {
        data object Loading : TransactionHistoryUiState
        data class Success(val transactions: List<Transaction>) : TransactionHistoryUiState
        data class Error(val message: String) : TransactionHistoryUiState
    }
}
