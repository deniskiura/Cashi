package ke.kiura.cashi.data.repository

import ke.kiura.cashi.data.mapper.toDto
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of TransactionRepository
 * Coordinates between Firestore service and domain layer
 */
class TransactionRepositoryImpl(
) : TransactionRepository {
    override fun getTransactions(): Flow<DomainState<List<Transaction>>> = flow {


    }

    override suspend fun saveTransaction(transaction: Transaction): DomainState<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getTransactionById(id: String): DomainState<Transaction> {
        TODO("Not yet implemented")
    }
}
