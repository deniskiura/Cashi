package ke.kiura.cashi.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ke.kiura.cashi.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY timestamp DESC")
    fun getTransactionsByStatus(status: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE recipientEmail = :email ORDER BY timestamp DESC")
    fun getTransactionsByRecipient(email: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

    @Query("SELECT SUM(amount) FROM transactions WHERE status = 'COMPLETED' AND currencyCode = :currencyCode")
    suspend fun getTotalAmountByCurrency(currencyCode: String): Double?
}
