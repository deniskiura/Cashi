package ke.kiura.cashi.domain.repository

import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction

interface PaymentRepository {
    suspend fun processPayment(payment: Payment): Result<Transaction>
}