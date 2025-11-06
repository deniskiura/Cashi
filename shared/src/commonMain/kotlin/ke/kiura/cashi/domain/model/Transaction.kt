package ke.kiura.cashi.domain.model

import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

/**
 * Domain model representing a completed payment transaction
 * Stored in Firestore and displayed in transaction history
 */
@Serializable
data class Transaction @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: Currency,
    val timestamp: Instant,
    val status: TransactionStatus
)

/**
 * Status of a payment transaction
 */
@Serializable
enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
}
