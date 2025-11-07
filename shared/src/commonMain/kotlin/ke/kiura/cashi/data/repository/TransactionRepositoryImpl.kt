package ke.kiura.cashi.data.repository

import ke.kiura.cashi.data.mapper.toDomain
import ke.kiura.cashi.data.mapper.toDomainList
import ke.kiura.cashi.data.mapper.toEntity
import ke.kiura.cashi.db.dao.TransactionDao
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.repository.TransactionRepository
import ke.kiura.cashi.remote.Remote
import ke.kiura.cashi.remote.RemoteApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * Implementation of TransactionRepository
 * Fetches transactions from Firebase and caches them locally in Room database
 * Uses offline-first approach: shows local data immediately, then syncs with Firebase
 */
class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val remoteApi: RemoteApi
) : TransactionRepository {

    override fun getTransactions(): Flow<DomainState<List<Transaction>>> {
        // Sync from Firebase in the background when this flow is started
        return transactionDao.getAllTransactions()
            .onStart {
                // Emit loading state first
                emit(emptyList())
                // Trigger sync in background
                CoroutineScope(Dispatchers.Default).launch {
                    syncFromFirebase()
                }
            }
            .map { entities ->
                // Map local database entities to domain transactions
                val transactions = entities.toDomainList()
                DomainState.Success(transactions)
            }
    }

    private suspend fun syncFromFirebase() {
        try {
            when (val result = remoteApi.getTransactions()) {
                is Remote.Success -> {
                    // Save all transactions from Firebase to local database
                    // Using insertTransactions for better performance
                    val entities = result.data.map { dto ->
                        val transaction = dto.toDomain()
                        transaction.toEntity()
                    }
                    transactionDao.insertTransactions(entities)
                }
                is Remote.Failure -> {
                    // Silently fail - we already have local data
                    // Could log error for monitoring
                }
                is Remote.UnAuthenticated -> {
                    // User needs to authenticate - could trigger auth flow
                }
                is Remote.ValidationError -> {
                    // Shouldn't happen for GET requests, but handle gracefully
                }
            }
        } catch (e: Exception) {
            // Silently fail - offline-first means we continue with local data
        }
    }
}
