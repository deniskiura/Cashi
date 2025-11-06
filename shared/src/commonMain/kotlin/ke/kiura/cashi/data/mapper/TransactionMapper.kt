package ke.kiura.cashi.data.mapper

import ke.kiura.cashi.db.entity.TransactionEntity
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.remote.dto.TransactionDto
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// DTO mappers
@OptIn(ExperimentalTime::class)
fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        id = id,
        recipient = recipient,
        amount = amount,
        currency = currency.code,
        timestamp = timestamp.toLong(),
        status = status.name
    )
}

@OptIn(ExperimentalTime::class)
fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        recipient = recipient,
        amount = amount,
        currency = Currency.fromCode(currency) ?: Currency.USD,
        timestamp = timestamp.toString(),
        status = when (status.uppercase()) {
            "PENDING" -> TransactionStatus.PENDING
            "COMPLETED" -> TransactionStatus.COMPLETED
            "FAILED" -> TransactionStatus.FAILED
            else -> TransactionStatus.PENDING
        }
    )
}

// Entity mappers
@OptIn(ExperimentalTime::class)
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        recipientEmail = recipient,
        amount = amount, // Already in cents
        currencyCode = currency.code,
        timestamp = timestamp.toLong(),
        status = status.name
    )
}

@OptIn(ExperimentalTime::class)
fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        recipient = recipientEmail,
        amount = amount, // Already in cents
        currency = Currency.fromCode(currencyCode) ?: Currency.USD,
        timestamp = timestamp.toString(),
        status = when (status.uppercase()) {
            "PENDING" -> TransactionStatus.PENDING
            "COMPLETED" -> TransactionStatus.COMPLETED
            "FAILED" -> TransactionStatus.FAILED
            else -> TransactionStatus.PENDING
        }
    )
}

// Payment to Entity mapper
@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
fun Payment.toEntity(
    id: String,
    timestamp: Long,
    status: TransactionStatus
): TransactionEntity {
    return TransactionEntity(
        id = id,
        recipientEmail = recipientEmail,
        amount = amount, // Already in cents (e.g., $1.00 = 100)
        currencyCode = currency.code,
        timestamp = timestamp,
        status = status.name
    )
}

// List mappers for convenience
@OptIn(ExperimentalTime::class)
fun List<Transaction>.toEntityList(): List<TransactionEntity> = map { it.toEntity() }

@OptIn(ExperimentalTime::class)
fun List<TransactionEntity>.toDomainList(): List<Transaction> = map { it.toDomain() }
