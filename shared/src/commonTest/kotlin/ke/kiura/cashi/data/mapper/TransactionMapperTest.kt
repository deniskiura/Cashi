package ke.kiura.cashi.data.mapper

import io.kotest.matchers.shouldBe
import ke.kiura.cashi.db.entity.TransactionEntity
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.remote.dto.TransactionDto
import kotlin.test.Test

class TransactionMapperTest {

    @Test
    fun `Transaction toDto should map correctly`() {
        // Given
        val transaction = Transaction(
            id = "test-id-123",
            recipient = "test@example.com",
            amount = 100,
            currency = Currency.USD,
            timestamp = "1672531200000",
            status = TransactionStatus.COMPLETED
        )

        // When
        val dto = transaction.toDto()

        // Then
        dto.id shouldBe "test-id-123"
        dto.recipient shouldBe "test@example.com"
        dto.amount shouldBe 100
        dto.currency shouldBe "USD"
        dto.timestamp shouldBe 1672531200000
        dto.status shouldBe "COMPLETED"
    }

    @Test
    fun `TransactionDto toDomain should map correctly with USD`() {
        // Given
        val dto = TransactionDto(
            id = "dto-id-456",
            recipient = "dto@example.com",
            amount = 250,
            currency = "USD",
            timestamp = 1672531200000, // 2023-01-01 00:00:00 UTC
            status = "COMPLETED"
        )

        // When
        val transaction = dto.toDomain()

        // Then
        transaction.id shouldBe "dto-id-456"
        transaction.recipient shouldBe "dto@example.com"
        transaction.amount shouldBe 250
        transaction.currency shouldBe Currency.USD
        transaction.status shouldBe TransactionStatus.COMPLETED
    }

    @Test
    fun `TransactionDto toDomain should handle PENDING status`() {
        // Given
        val dto = TransactionDto(
            id = "pending-id",
            recipient = "pending@example.com",
            amount = 100,
            currency = "EUR",
            timestamp = 1672531200000,
            status = "PENDING"
        )

        // When
        val transaction = dto.toDomain()

        // Then
        transaction.status shouldBe TransactionStatus.PENDING
        transaction.currency shouldBe Currency.EUR
    }

    @Test
    fun `TransactionDto toDomain should handle FAILED status`() {
        // Given
        val dto = TransactionDto(
            id = "failed-id",
            recipient = "failed@example.com",
            amount = 100,
            currency = "EUR",
            timestamp = 1672531200000,
            status = "FAILED"
        )

        // When
        val transaction = dto.toDomain()

        // Then
        transaction.status shouldBe TransactionStatus.FAILED
        transaction.currency shouldBe Currency.EUR
    }

    @Test
    fun `TransactionDto toDomain should default to PENDING for unknown status`() {
        // Given
        val dto = TransactionDto(
            id = "unknown-id",
            recipient = "unknown@example.com",
            amount = 100,
            currency = "USD",
            timestamp = 1672531200000,
            status = "UNKNOWN_STATUS"
        )

        // When
        val transaction = dto.toDomain()

        // Then
        transaction.status shouldBe TransactionStatus.PENDING
    }

    @Test
    fun `TransactionDto toDomain should default to USD for unknown currency`() {
        // Given
        val dto = TransactionDto(
            id = "currency-id",
            recipient = "currency@example.com",
            amount = 100,
            currency = "INVALID",
            timestamp = 1672531200000,
            status = "COMPLETED"
        )

        // When
        val transaction = dto.toDomain()

        // Then
        transaction.currency shouldBe Currency.USD
    }

    @Test
    fun `Transaction toEntity should map correctly`() {
        // Given
        val transaction = Transaction(
            id = "entity-id-789",
            recipient = "entity@example.com",
            amount = 300,
            currency = Currency.EUR,
            timestamp = "1672531200000",
            status = TransactionStatus.COMPLETED
        )

        // When
        val entity = transaction.toEntity()

        // Then
        entity.id shouldBe "entity-id-789"
        entity.recipientEmail shouldBe "entity@example.com"
        entity.amount shouldBe 300
        entity.currencyCode shouldBe "EUR"
        entity.timestamp shouldBe 1672531200000
        entity.status shouldBe "COMPLETED"
    }

    @Test
    fun `TransactionEntity toDomain should map correctly`() {
        // Given
        val entity = TransactionEntity(
            id = "entity-domain-123",
            recipientEmail = "entity-domain@example.com",
            amount = 400,
            currencyCode = "USD",
            timestamp = 1672531200000,
            status = "PENDING"
        )

        // When
        val transaction = entity.toDomain()

        // Then
        transaction.id shouldBe "entity-domain-123"
        transaction.recipient shouldBe "entity-domain@example.com"
        transaction.amount shouldBe 400
        transaction.currency shouldBe Currency.USD
        transaction.status shouldBe TransactionStatus.PENDING
    }

    @Test
    fun `TransactionEntity toDto should map correctly`() {
        // Given
        val entity = TransactionEntity(
            id = "entity-dto-456",
            recipientEmail = "entity-dto@example.com",
            amount = 500,
            currencyCode = "EUR",
            timestamp = 1672531200000,
            status = "FAILED"
        )

        // When
        val dto = entity.toDto()

        // Then
        dto.id shouldBe "entity-dto-456"
        dto.recipient shouldBe "entity-dto@example.com"
        dto.amount shouldBe 500
        dto.currency shouldBe "EUR"
        dto.timestamp shouldBe 1672531200000
        dto.status shouldBe "FAILED"
    }

    @Test
    fun `Payment toEntity should map correctly`() {
        // Given
        val payment = Payment(
            recipientEmail = "payment@example.com",
            amount = 600,
            currency = Currency.USD
        )
        val id = "payment-id-123"
        val timestamp = 1672531200000L
        val status = TransactionStatus.PENDING

        // When
        val entity = payment.toEntity(id, timestamp, status)

        // Then
        entity.id shouldBe "payment-id-123"
        entity.recipientEmail shouldBe "payment@example.com"
        entity.amount shouldBe 600
        entity.currencyCode shouldBe "USD"
        entity.timestamp shouldBe 1672531200000
        entity.status shouldBe "PENDING"
    }

    @Test
    fun `Payment toEntity should handle different currencies`() {
        // Given & When & Then
        val currencies = listOf(Currency.USD, Currency.EUR)
        currencies.forEach { currency ->
            val payment = Payment(
                recipientEmail = "multi@example.com",
                amount = 100,
                currency = currency
            )
            val entity = payment.toEntity("id", 1672531200000L, TransactionStatus.PENDING)
            entity.currencyCode shouldBe currency.code
        }
    }

    @Test
    fun `List mappers should work correctly - toEntityList`() {
        // Given
        val transactions = listOf(
            Transaction("id1", "user1@example.com", 100, Currency.USD, "1672531200000", TransactionStatus.COMPLETED),
            Transaction("id2", "user2@example.com", 200, Currency.EUR, "1672531200000", TransactionStatus.PENDING)
        )

        // When
        val entities = transactions.toEntityList()

        // Then
        entities.size shouldBe 2
        entities[0].id shouldBe "id1"
        entities[0].recipientEmail shouldBe "user1@example.com"
        entities[1].id shouldBe "id2"
        entities[1].recipientEmail shouldBe "user2@example.com"
    }

    @Test
    fun `List mappers should work correctly - toDomainList`() {
        // Given
        val entities = listOf(
            TransactionEntity("id1", "user1@example.com", 100, "USD", 1672531200000, "COMPLETED"),
            TransactionEntity("id2", "user2@example.com", 200, "EUR", 1672531200000, "PENDING")
        )

        // When
        val transactions = entities.toDomainList()

        // Then
        transactions.size shouldBe 2
        transactions[0].id shouldBe "id1"
        transactions[0].recipient shouldBe "user1@example.com"
        transactions[1].id shouldBe "id2"
        transactions[1].recipient shouldBe "user2@example.com"
    }

    @Test
    fun `Transaction toEntity should handle amounts correctly in cents`() {
        // Given
        val transaction = Transaction(
            id = "cents-id",
            recipient = "cents@example.com",
            amount = 12345, // $123.45 in cents
            currency = Currency.USD,
            timestamp = "1672531200000",
            status = TransactionStatus.COMPLETED
        )

        // When
        val entity = transaction.toEntity()

        // Then
        entity.amount shouldBe 12345
    }

    @Test
    fun `Payment toEntity with COMPLETED status should map correctly`() {
        // Given
        val payment = Payment(
            recipientEmail = "completed@example.com",
            amount = 700,
            currency = Currency.EUR
        )

        // When
        val entity = payment.toEntity("comp-id", 1672531200000L, TransactionStatus.COMPLETED)

        // Then
        entity.status shouldBe "COMPLETED"
    }

    @Test
    fun `Payment toEntity with FAILED status should map correctly`() {
        // Given
        val payment = Payment(
            recipientEmail = "failed@example.com",
            amount = 800,
            currency = Currency.USD
        )

        // When
        val entity = payment.toEntity("fail-id", 1672531200000L, TransactionStatus.FAILED)

        // Then
        entity.status shouldBe "FAILED"
    }
}
