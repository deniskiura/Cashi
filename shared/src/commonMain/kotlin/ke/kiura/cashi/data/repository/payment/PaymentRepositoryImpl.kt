package ke.kiura.cashi.data.repository.payment

import ke.kiura.cashi.data.mapper.toDomain
import ke.kiura.cashi.data.mapper.toDto
import ke.kiura.cashi.data.mapper.toEntity
import ke.kiura.cashi.db.dao.TransactionDao
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.domain.repository.PaymentRepository
import ke.kiura.cashi.remote.Remote
import ke.kiura.cashi.remote.RemoteApi
import kotlin.time.Clock.System.now
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Implementation of PaymentRepository
 * Coordinates between API service and domain layer
 */
class PaymentRepositoryImpl(
    private val remoteApi: RemoteApi,
    private val transactionDao: TransactionDao
) : PaymentRepository {

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun processPayment(payment: Payment): DomainState<Transaction> {

        // Generate transaction ID and timestamp
        val transactionId = Uuid.random().toString()
        val timestamp = now().toEpochMilliseconds()

        // Create pending transaction entity
        val transactionEntity = payment.toEntity(
            id = transactionId,
            timestamp = timestamp,
            status = TransactionStatus.entries.random()  // Random status for demo purposes
        )

        // Save to local database first
        transactionDao.insertTransaction(transactionEntity)

        // Send to remote API
        return when (val result = remoteApi.saveTransaction(transactionEntity.toDto())) {
            is Remote.Success -> {
                // Update status to COMPLETED
                val completedEntity =
                    transactionEntity.copy(status = TransactionStatus.COMPLETED.name)
                transactionDao.updateTransaction(completedEntity)
                DomainState.Success(completedEntity.toDomain())
            }

            is Remote.Failure -> {
                // Update status to FAILED
                val failedEntity = transactionEntity.copy(status = TransactionStatus.FAILED.name)
                transactionDao.insertTransaction(failedEntity)
                DomainState.Error(result.error)
            }

            is Remote.ValidationError -> {
                // Update status to FAILED
                val failedEntity = transactionEntity.copy(status = TransactionStatus.FAILED.name)
                transactionDao.insertTransaction(failedEntity)
                val errorMsg = result.errors.entries.joinToString(", ") {
                    "${it.key}: ${it.value.joinToString()}"
                }
                DomainState.Error("Validation failed: $errorMsg")
            }

            is Remote.UnAuthenticated -> {
                // Update status to FAILED
                val failedEntity = transactionEntity.copy(status = TransactionStatus.FAILED.name)
                transactionDao.insertTransaction(failedEntity)
                DomainState.Error("Authentication required")
            }
        }
    }
}

