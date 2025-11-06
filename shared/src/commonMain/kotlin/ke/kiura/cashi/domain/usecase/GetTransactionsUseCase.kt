package ke.kiura.cashi.domain.usecase

import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving transaction history
 * Returns a Flow for real-time updates from Firestore
 */
class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<Result<List<Transaction>>> {
        return transactionRepository.getTransactions()
    }
}
