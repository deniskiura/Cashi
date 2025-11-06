package ke.kiura.cashi.domain.usecase

import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.repository.PaymentRepository


class SendPaymentUseCase(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(payment: Payment): DomainState<Transaction> {
        val validationResult = payment.validate()
        if (!validationResult.isValid()) {
            return DomainState.Error(validationResult.errorMessage() ?: "Invalid payment data")
        }
        return paymentRepository.processPayment(payment)
    }
}
