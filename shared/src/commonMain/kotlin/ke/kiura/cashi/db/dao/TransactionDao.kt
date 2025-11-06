package ke.kiura.cashi.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ke.kiura.cashi.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Transaction operations
 */
@Dao
interface TransactionDao {

    /**
     * Insert a single transaction
     * Replaces existing transaction if there's a conflict
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    /**
     * Insert multiple transactions
     * Replaces existing transactions if there's a conflict
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    /**
     * Update an existing transaction
     */
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    /**
     * Delete a transaction
     */
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    /**
     * Get all transactions as a Flow
     * Automatically updates when data changes
     */
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    /**
     * Get a single transaction by ID
     */
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    /**
     * Get transactions by status
     */
    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY timestamp DESC")
    fun getTransactionsByStatus(status: String): Flow<List<TransactionEntity>>

    /**
     * Get transactions by recipient email
     */
    @Query("SELECT * FROM transactions WHERE recipientEmail = :email ORDER BY timestamp DESC")
    fun getTransactionsByRecipient(email: String): Flow<List<TransactionEntity>>

    /**
     * Get transactions within a date range
     */
    @Query("SELECT * FROM transactions WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<TransactionEntity>>

    /**
     * Delete all transactions
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    /**
     * Get total count of transactions
     */
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

    /**
     * Get sum of all completed transactions in a specific currency
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE status = 'COMPLETED' AND currencyCode = :currencyCode")
    suspend fun getTotalAmountByCurrency(currencyCode: String): Double?
}
