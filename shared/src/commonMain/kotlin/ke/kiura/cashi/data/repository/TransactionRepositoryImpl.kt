package ke.kiura.composer.data.repository

import ke.kiura.cashi.data.mapper.toDomain
import ke.kiura.cashi.data.mapper.toDto
import ke.kiura.composer.data.remote.firebase.FirestoreService
import ke.kiura.composer.domain.model.Transaction
import ke.kiura.composer.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of TransactionRepository
 * Coordinates between Firestore service and domain layer
 */
class TransactionRepositoryImpl(
    private val firestoreService: FirestoreService
) : TransactionRepository {

    override fun getTransactions(): Flow<Result<List<Transaction>>> {
        return firestoreService.getTransactions().map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.map { it.toDomain() })
                is Result.Error -> Result.Error(result.exception)
            }
        }
    }

    override suspend fun saveTransaction(transaction: Transaction): Result<Unit> {
        val dto = transaction.toDto()
        return firestoreService.saveTransaction(dto)
    }

    override suspend fun getTransactionById(id: String): Result<Transaction> {
        val result = firestoreService.getTransactionById(id)
        return when (result) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.exception)
        }
    }
}
