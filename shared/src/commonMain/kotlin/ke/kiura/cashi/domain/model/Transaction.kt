package ke.kiura.cashi.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime


@Serializable
data class Transaction @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val recipient: String,
    val amount: Int,
    val currency: Currency,
    val timestamp: String,
    val status: TransactionStatus
)


@Serializable
enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED
}
