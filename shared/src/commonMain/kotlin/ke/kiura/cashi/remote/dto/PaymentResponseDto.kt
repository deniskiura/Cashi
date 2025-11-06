package ke.kiura.cashi.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for payment API responses
 */
@Serializable
data class PaymentResponseDto(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val timestamp: Long,
    val status: String
)
