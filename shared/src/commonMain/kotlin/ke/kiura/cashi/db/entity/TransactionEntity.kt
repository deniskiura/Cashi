package ke.kiura.cashi.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val recipientEmail: String,
    val amount: Int,
    val currencyCode: String,
    val timestamp: Long,
    val status: String
)
