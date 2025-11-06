package ke.kiura.composer.domain.usecase

import ke.kiura.cashi.domain.repository.TransactionRepository
import ke.kiura.cashi.domain.repository.PaymentRepository
import ke.kiura.composer.domain.common.AppException
import ke.kiura.composer.domain.common.Result
import ke.kiura.composer.domain.model.Payment
import ke.kiura.composer.domain.model.Transaction
import ke.kiura.composer.domain.repository.PaymentRepository
import ke.kiura.composer.domain.repository.TransactionRepository

/**
 * Use case for sending a payment
 * Encapsulates the business logic for payment processing
 *
 * Steps:
 * 1. Validate payment data
 * 2. Process payment through API
 * 3. Save transaction to Firestore
 */
class SendPaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(payment: Payment): Result<Transaction> {
        // Step 1: Validate payment
        val validationResult = payment.validate()
        if (!validationResult.isValid()) {
            return Result.Error(
                AppException.ValidationError(
                    validationResult.errorMessage() ?: "Invalid payment data"
                )
            )
        }

        // Step 2: Process payment through API
        val paymentResult = paymentRepository.processPayment(payment)
        if (paymentResult.isError()) {
            return paymentResult
        }

        val transaction = paymentResult.getOrNull()
            ?: return Result.Error(AppException.UnknownError("Transaction data is null"))

        // Step 3: Save transaction to Firestore
        val saveResult = transactionRepository.saveTransaction(transaction)
        if (saveResult.isError()) {
            // Payment processed but save failed - log this for monitoring
            // In production, you might want to retry or queue this
            return Result.Error(
                saveResult.exceptionOrNull() ?: AppException.FirestoreError()
            )
        }

        return Result.Success(transaction)
    }
}
