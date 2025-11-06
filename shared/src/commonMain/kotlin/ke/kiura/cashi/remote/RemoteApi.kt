package ke.kiura.cashi.remote

import ke.kiura.cashi.remote.dto.TransactionDto

interface RemoteApi {
    suspend fun saveTransaction(transaction: TransactionDto): Remote<Unit>
}