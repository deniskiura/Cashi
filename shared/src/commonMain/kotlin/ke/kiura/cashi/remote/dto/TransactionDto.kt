package ke.kiura.cashi.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Firestore transaction documents
 * Matches the Firestore document structure
 */
@Serializable
data class TransactionDto(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val timestamp: Long,
    val status: String
)
