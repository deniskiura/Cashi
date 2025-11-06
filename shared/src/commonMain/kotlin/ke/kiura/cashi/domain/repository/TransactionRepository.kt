package ke.kiura.cashi.domain.repository

import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.composer.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for transaction operations
 * Defines the contract for accessing transaction history
 * Following Clean Architecture principles - domain layer defines the interface
 */
interface TransactionRepository {

    fun getTransactions(): Flow<Result<List<Transaction>>>

    /**
     * Save a transaction to Firestore
     * @param transaction The transaction to save
     * @return Result indicating success or failure
     */
    suspend fun saveTransaction(transaction: Transaction): Result<Unit>

    /**
     * Get a specific transaction by ID
     * @param id The transaction ID
     * @return Result containing the transaction or an error
     */
    suspend fun getTransactionById(id: String): Result<Transaction>
}
