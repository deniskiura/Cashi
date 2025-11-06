package ke.kiura.cashi.data.repository

import ke.kiura.cashi.data.mapper.toDomainList
import ke.kiura.cashi.db.dao.TransactionDao
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of TransactionRepository
 * Fetches transactions from local Room database
 */
class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getTransactions(): Flow<DomainState<List<Transaction>>> = flow {

        emit(DomainState.Loading)

        transactionDao.getAllTransactions().collect { entities ->
            val transactions = entities.toDomainList()
            emit(DomainState.Success(transactions))
        }
    }

}
