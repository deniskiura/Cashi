package ke.kiura.cashi.data.mapper

import ke.kiura.cashi.db.entity.TransactionEntity
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.remote.dto.TransactionDto
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

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
        timestamp = formatTimestamp(timestamp),
        status = when (status.uppercase()) {
            "PENDING" -> TransactionStatus.PENDING
            "COMPLETED" -> TransactionStatus.COMPLETED
            "FAILED" -> TransactionStatus.FAILED
            else -> TransactionStatus.PENDING
        }
    )
}

@OptIn(ExperimentalTime::class)
fun TransactionEntity.toDto(): TransactionDto {
    return TransactionDto(
        id = id,
        recipient = recipientEmail,
        amount = amount, // Already in cents
        currency = currencyCode,
        timestamp = timestamp,
        status = status
    )
}

// Helper function to format timestamp to readable format
@OptIn(ExperimentalTime::class)
private fun formatTimestamp(timestampMillis: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestampMillis)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    // Format as "Jan 15, 2:30 PM"
    val month = when (dateTime.month.ordinal + 1) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
        5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
        9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> ""
    }

    val hour = if (dateTime.hour == 0) 12 else if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
    val amPm = if (dateTime.hour < 12) "AM" else "PM"
    val minute = dateTime.minute.toString().padStart(2, '0')

    return "$month ${dateTime.day}, $hour:$minute $amPm"
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
