package ke.kiura.cashi.domain.usecase

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.Transaction
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.domain.repository.PaymentRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SendPaymentUseCaseTest {

    private val paymentRepository: PaymentRepository = mockk()
    private val useCase = SendPaymentUseCase(paymentRepository)

    @Test
    fun `invoke should return Error when payment has invalid email`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "invalid-email",
            amount = 100,
            currency = Currency.USD
        )

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Error>()
        (result as DomainState.Error).message shouldBe "Invalid email format"

        // Verify repository was not called
        coVerify(exactly = 0) { paymentRepository.processPayment(any()) }
    }

    @Test
    fun `invoke should return Error when payment has zero amount`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 0,
            currency = Currency.USD
        )

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Error>()
        (result as DomainState.Error).message shouldBe "Amount must be greater than zero"

        // Verify repository was not called
        coVerify(exactly = 0) { paymentRepository.processPayment(any()) }
    }

    @Test
    fun `invoke should return Error when payment has negative amount`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = -100,
            currency = Currency.USD
        )

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Error>()
        (result as DomainState.Error).message shouldBe "Amount must be greater than zero"

        // Verify repository was not called
        coVerify(exactly = 0) { paymentRepository.processPayment(any()) }
    }

    @Test
    fun `invoke should return Success when payment is valid and repository succeeds`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 100,
            currency = Currency.USD
        )

        val expectedTransaction = Transaction(
            id = "test-id",
            recipient = "test@example.com",
            amount = 100,
            currency = Currency.USD,
            timestamp = "Jan 1, 12:00 PM",
            status = TransactionStatus.COMPLETED
        )

        coEvery { paymentRepository.processPayment(payment) } returns DomainState.Success(expectedTransaction)

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<Transaction>>()
        (result as DomainState.Success).data shouldBe expectedTransaction

        // Verify repository was called
        coVerify(exactly = 1) { paymentRepository.processPayment(payment) }
    }

    @Test
    fun `invoke should return Error when payment is valid but repository fails`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 100,
            currency = Currency.USD
        )

        val errorMessage = "Network error"
        coEvery { paymentRepository.processPayment(payment) } returns DomainState.Error(errorMessage)

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Error>()
        (result as DomainState.Error).message shouldBe errorMessage

        // Verify repository was called
        coVerify(exactly = 1) { paymentRepository.processPayment(payment) }
    }

    @Test
    fun `invoke should process valid payment with EUR currency`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "euro@example.com",
            amount = 250,
            currency = Currency.EUR
        )

        val expectedTransaction = Transaction(
            id = "eur-id",
            recipient = "euro@example.com",
            amount = 250,
            currency = Currency.EUR,
            timestamp = "Jan 1, 12:00 PM",
            status = TransactionStatus.COMPLETED
        )

        coEvery { paymentRepository.processPayment(payment) } returns DomainState.Success(expectedTransaction)

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<Transaction>>()
        (result as DomainState.Success).data.currency shouldBe Currency.EUR

        coVerify(exactly = 1) { paymentRepository.processPayment(payment) }
    }

    @Test
    fun `invoke should process valid payment with USD currency`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "usd@example.com",
            amount = 150,
            currency = Currency.USD
        )

        val expectedTransaction = Transaction(
            id = "usd-id",
            recipient = "usd@example.com",
            amount = 150,
            currency = Currency.USD,
            timestamp = "Jan 1, 12:00 PM",
            status = TransactionStatus.COMPLETED
        )

        coEvery { paymentRepository.processPayment(payment) } returns DomainState.Success(expectedTransaction)

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<Transaction>>()
        (result as DomainState.Success).data.currency shouldBe Currency.USD

        coVerify(exactly = 1) { paymentRepository.processPayment(payment) }
    }

    @Test
    fun `invoke should handle very large amounts`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 10000000, // $100,000.00
            currency = Currency.USD
        )

        val expectedTransaction = Transaction(
            id = "large-id",
            recipient = "test@example.com",
            amount = 10000000,
            currency = Currency.USD,
            timestamp = "Jan 1, 12:00 PM",
            status = TransactionStatus.COMPLETED
        )

        coEvery { paymentRepository.processPayment(payment) } returns DomainState.Success(expectedTransaction)

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<Transaction>>()

        coVerify(exactly = 1) { paymentRepository.processPayment(payment) }
    }

    @Test
    fun `invoke should handle email with special characters`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "user+tag@example.com",
            amount = 100,
            currency = Currency.USD
        )

        val expectedTransaction = Transaction(
            id = "special-id",
            recipient = "user+tag@example.com",
            amount = 100,
            currency = Currency.USD,
            timestamp = "Jan 1, 12:00 PM",
            status = TransactionStatus.COMPLETED
        )

        coEvery { paymentRepository.processPayment(payment) } returns DomainState.Success(expectedTransaction)

        // When
        val result = useCase(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<Transaction>>()

        coVerify(exactly = 1) { paymentRepository.processPayment(payment) }
    }
}
