package ke.kiura.cashi.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a transaction in the local database
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currencyCode: String,
    val timestamp: Long,
    val status: String
)
