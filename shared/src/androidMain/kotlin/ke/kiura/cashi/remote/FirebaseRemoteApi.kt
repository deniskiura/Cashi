package ke.kiura.cashi.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ke.kiura.cashi.remote.dto.TransactionDto
import kotlinx.coroutines.tasks.await

class FirebaseRemoteApi(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : RemoteApi {

    companion object {
        private const val TRANSACTIONS_COLLECTION = "transactions"
    }

    override suspend fun saveTransaction(transaction: TransactionDto): Remote<Unit> {

        // Save to Firestore
        firestore.collection(TRANSACTIONS_COLLECTION)
            .document(transaction.id)
            .set(
                mapOf(
                    "id" to transaction.id,
                    "recipient" to transaction.recipient,
                    "amount" to transaction.amount,
                    "currency" to transaction.currency,
                    "timestamp" to transaction.timestamp,
                    "status" to transaction.status
                )
            )
            .await()

        return Remote.Success(Unit)

    }

    override suspend fun getTransactions(): Remote<List<TransactionDto>> {
        return try {
            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val transactions = snapshot.documents.mapNotNull { document ->
                TransactionDto(
                    id = document.getString("id") ?: return@mapNotNull null,
                    recipient = document.getString("recipient") ?: return@mapNotNull null,
                    amount = document.getLong("amount")?.toInt() ?: return@mapNotNull null,
                    currency = document.getString("currency") ?: return@mapNotNull null,
                    timestamp = document.getLong("timestamp") ?: return@mapNotNull null,
                    status = document.getString("status") ?: return@mapNotNull null
                )
            }

            Remote.Success(transactions)
        } catch (e: Exception) {
            when {
                e.message?.contains("PERMISSION_DENIED") == true -> {
                    Remote.UnAuthenticated
                }

                e.message?.contains("UNAVAILABLE") == true -> {
                    Remote.Failure("Network connection failed. Please check your internet connection.")
                }

                else -> {
                    Remote.Failure(e.message ?: "Failed to fetch transactions")
                }
            }
        }
    }
}
