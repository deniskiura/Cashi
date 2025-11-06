package ke.kiura.cashi.data.repository

import ke.kiura.cashi.data.mapper.toDto
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.repository.PaymentRepository
import ke.kiura.cashi.remote.RemoteApi

/**
 * Implementation of PaymentRepository
 * Coordinates between API service and domain layer
 */
class PaymentRepositoryImpl(
    private val remoteApi: RemoteApi
) : PaymentRepository {

    override suspend fun processPayment(payment: Payment): Result<Transaction> {
        // Convert domain model to DTO
        val requestDto = payment.toDto()

        // Call API service
        val result = remoteApi.saveTransaction(requestDto)

        // Map result back to domain model
        return when (result) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.exception)
        }
    }
}
