package ke.kiura.cashi.data.mapper

import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.remote.dto.PaymentRequestDto
import ke.kiura.cashi.remote.dto.PaymentResponseDto
import kotlin.time.ExperimentalTime

import kotlin.time.Instant

fun Payment.toDto(): PaymentRequestDto {
    return PaymentRequestDto(
        recipientEmail = recipientEmail,
        amount = amount,
        currency = currency.code
    )
}

@OptIn(ExperimentalTime::class)
fun PaymentResponseDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        recipient = recipientEmail,
        amount = amount,
        currency = Currency.fromCode(currency) ?: Currency.USD,
        timestamp = Instant.fromEpochMilliseconds(timestamp).toString(),
        status = when (status.uppercase()) {
            "PENDING" -> TransactionStatus.PENDING
            "COMPLETED" -> TransactionStatus.COMPLETED
            "FAILED" -> TransactionStatus.FAILED
            else -> TransactionStatus.PENDING
        }
    )
}
