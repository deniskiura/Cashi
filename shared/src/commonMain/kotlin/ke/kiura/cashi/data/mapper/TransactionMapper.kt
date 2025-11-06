package ke.kiura.cashi.data.mapper

import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.remote.dto.TransactionDto
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        id = id,
        recipientEmail = recipientEmail,
        amount = amount,
        currency = currency.code,
        timestamp = timestamp.toEpochMilliseconds(),
        status = status.name
    )
}

@OptIn(ExperimentalTime::class)
fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        recipientEmail = recipientEmail,
        amount = amount,
        currency = Currency.fromCode(currency) ?: Currency.USD,
        timestamp = Instant.fromEpochMilliseconds(timestamp),
        status = when (status.uppercase()) {
            "PENDING" -> TransactionStatus.PENDING
            "COMPLETED" -> TransactionStatus.COMPLETED
            "FAILED" -> TransactionStatus.FAILED
            else -> TransactionStatus.PENDING
        }
    )
}
