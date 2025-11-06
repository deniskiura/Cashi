package ke.kiura.cashi.remote

import ke.kiura.cashi.remote.dto.TransactionDto

interface RemoteApi {
    suspend fun getTransactions(): Remote<List<TransactionDto>>
    suspend fun saveTransaction(transaction: TransactionDto): Remote<Unit>
    suspend fun getTransactionById(id: String): Remote<TransactionDto>
}