package ke.kiura.cashi.domain.model

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class PaymentTest {

    @Test
    fun `validate should return Valid for valid payment`() {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 100, // $1.00 in cents
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid() shouldBe true
        result.errorMessage() shouldBe null
    }

    @Test
    fun `validate should return Invalid for invalid email format - no at symbol`() {
        // Given
        val payment = Payment(
            recipientEmail = "invalid-email.com",
            amount = 100,
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.isValid() shouldBe false
        result.errorMessage() shouldBe "Invalid email format"
    }

    @Test
    fun `validate should return Invalid for invalid email format - no domain`() {
        // Given
        val payment = Payment(
            recipientEmail = "invalid@",
            amount = 100,
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.isValid() shouldBe false
        result.errorMessage() shouldBe "Invalid email format"
    }

    @Test
    fun `validate should return Invalid for empty email`() {
        // Given
        val payment = Payment(
            recipientEmail = "",
            amount = 100,
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.isValid() shouldBe false
        result.errorMessage() shouldBe "Invalid email format"
    }

    @Test
    fun `validate should return Invalid for zero amount`() {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 0,
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.isValid() shouldBe false
        result.errorMessage() shouldBe "Amount must be greater than zero"
    }

    @Test
    fun `validate should return Invalid for negative amount`() {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = -100,
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.isValid() shouldBe false
        result.errorMessage() shouldBe "Amount must be greater than zero"
    }

    @Test
    fun `validate should accept valid email with plus sign`() {
        // Given
        val payment = Payment(
            recipientEmail = "test+tag@example.com",
            amount = 100,
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid() shouldBe true
    }

    @Test
    fun `validate should accept valid email with dots`() {
        // Given
        val payment = Payment(
            recipientEmail = "first.last@example.co.uk",
            amount = 100,
            currency = Currency.EUR
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid() shouldBe true
    }

    @Test
    fun `validate should accept valid email with hyphen`() {
        // Given
        val payment = Payment(
            recipientEmail = "test-user@example.com",
            amount = 100,
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid() shouldBe true
    }

    @Test
    fun `validate should accept large amounts`() {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 100000000, // $1,000,000.00 in cents
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid() shouldBe true
    }

    @Test
    fun `validate should accept small amounts`() {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 1, // $0.01 in cents
            currency = Currency.USD
        )

        // When
        val result = payment.validate()

        // Then
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid() shouldBe true
    }

    @Test
    fun `validate should work with different currencies`() {
        // Given & When & Then
        listOf(Currency.USD, Currency.EUR).forEach { currency ->
            val payment = Payment(
                recipientEmail = "test@example.com",
                amount = 100,
                currency = currency
            )
            val result = payment.validate()
            result.shouldBeInstanceOf<ValidationResult.Valid>()
        }
    }
}
