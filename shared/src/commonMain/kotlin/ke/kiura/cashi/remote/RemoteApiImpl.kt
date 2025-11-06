package ke.kiura.cashi.remote

import ke.kiura.cashi.remote.dto.TransactionDto
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * We have not implemented the API here so we will simulate different responses
 * In a typical app this would be done using Ktor HTTP client
 */
class RemoteApiImpl : RemoteApi {
    override suspend fun saveTransaction(transaction: TransactionDto): Remote<Unit> {
        // Simulate network delay
        delay(1000)

        // Randomly simulate different API response states
        // This helps test all UI states (success, error, validation, auth)
        return when (Random.nextInt(0, 5)) {
            in 0..2 -> Remote.Success(Unit) // more % success rate
            3 -> Remote.ValidationError(
                mapOf(
                    "amount" to listOf("Amount exceeds daily limit"),
                    "recipient" to listOf("Recipient email not found in system")
                )
            )
            4 -> Remote.UnAuthenticated // small % authentication error
            else -> Remote.Failure("Network error: Unable to connect to payment service")
        }
    }
}
