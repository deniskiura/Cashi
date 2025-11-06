package ke.kiura.cashi.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for payment API requests
 * Separates network layer from domain layer
 */
@Serializable
data class PaymentRequestDto(
    val recipientEmail: String,
    val amount: Int,
    val currency: String
)
