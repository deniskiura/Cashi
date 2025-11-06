package ke.kiura.cashi.domain.repository

import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<DomainState<List<Transaction>>>

    suspend fun saveTransaction(transaction: Transaction): DomainState<Unit>

    suspend fun getTransactionById(id: String): DomainState<Transaction>
}
