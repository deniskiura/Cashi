package ke.kiura.cashi.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for Firestore transaction documents
 * Matches the Firestore document structure
 */
@Serializable
data class TransactionDto(
    val id: String,
    val recipient: String,
    val amount: Int, // 1.00$ is represented as 100
    val currency: String,
    val timestamp: Long,
    val status: String
)
