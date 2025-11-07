package ke.kiura.cashi.data.repository.payment

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import ke.kiura.cashi.db.dao.TransactionDao
import ke.kiura.cashi.db.entity.TransactionEntity
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.model.TransactionStatus
import ke.kiura.cashi.remote.Remote
import ke.kiura.cashi.remote.RemoteApi
import ke.kiura.cashi.remote.dto.TransactionDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PaymentRepositoryImplTest {

    private val remoteApi: RemoteApi = mockk()
    private val transactionDao: TransactionDao = mockk()
    private val repository = PaymentRepositoryImpl(remoteApi, transactionDao)

    @Test
    fun `processPayment should return Success when API call succeeds`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "success@example.com",
            amount = 100,
            currency = Currency.USD
        )

        val entitySlot = slot<TransactionEntity>()
        coEvery { transactionDao.insertTransaction(capture(entitySlot)) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<*>>()
        val transaction = (result as DomainState.Success).data
        transaction.recipient shouldBe "success@example.com"
        transaction.amount shouldBe 100
        transaction.currency shouldBe Currency.USD
        transaction.status shouldBe TransactionStatus.COMPLETED

        // Verify DAO interactions
        coVerify(exactly = 1) { transactionDao.insertTransaction(any()) }
        coVerify(exactly = 1) { transactionDao.updateTransaction(any()) }

        // Verify the entity was saved with PENDING status first
        entitySlot.captured.status shouldBe "PENDING"
        entitySlot.captured.recipientEmail shouldBe "success@example.com"
    }

    @Test
    fun `processPayment should return Error when API call fails`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "failure@example.com",
            amount = 200,
            currency = Currency.EUR
        )

        val errorMessage = "Network connection failed"
        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Failure(errorMessage)

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Error>()
        (result as DomainState.Error).message shouldBe errorMessage

        // Verify DAO interactions - should insert twice (pending and failed)
        coVerify(exactly = 2) { transactionDao.insertTransaction(any()) }
        coVerify(exactly = 0) { transactionDao.updateTransaction(any()) }
    }

    @Test
    fun `processPayment should return Error when API returns ValidationError`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "validation@example.com",
            amount = 300,
            currency = Currency.EUR
        )

        val validationErrors = mapOf(
            "email" to listOf("Invalid format"),
            "amount" to listOf("Too small")
        )
        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.ValidationError(validationErrors)

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Error>()
        val errorMessage = (result as DomainState.Error).message
        errorMessage shouldContain "Validation failed"
        errorMessage shouldContain "email"
        errorMessage shouldContain "Invalid format"

        // Verify DAO interactions
        coVerify(exactly = 2) { transactionDao.insertTransaction(any()) }
    }

    @Test
    fun `processPayment should return Error when API returns UnAuthenticated`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "auth@example.com",
            amount = 400,
            currency = Currency.USD
        )

        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.UnAuthenticated

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Error>()
        (result as DomainState.Error).message shouldBe "Authentication required"

        // Verify DAO interactions
        coVerify(exactly = 2) { transactionDao.insertTransaction(any()) }
    }

    @Test
    fun `processPayment should save pending transaction to database before API call`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "pending@example.com",
            amount = 500,
            currency = Currency.USD
        )

        val insertedEntities = mutableListOf<TransactionEntity>()
        coEvery { transactionDao.insertTransaction(capture(insertedEntities)) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When
        repository.processPayment(payment)

        // Then
        insertedEntities.size shouldBe 1
        insertedEntities[0].status shouldBe "PENDING"
        insertedEntities[0].recipientEmail shouldBe "pending@example.com"
        insertedEntities[0].amount shouldBe 500
        insertedEntities[0].currencyCode shouldBe "USD"
    }

    @Test
    fun `processPayment should update status to COMPLETED on success`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "completed@example.com",
            amount = 600,
            currency = Currency.USD
        )

        val updatedEntitySlot = slot<TransactionEntity>()
        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { transactionDao.updateTransaction(capture(updatedEntitySlot)) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<*>>()
        updatedEntitySlot.captured.status shouldBe "COMPLETED"

        coVerify(exactly = 1) { transactionDao.updateTransaction(any()) }
    }

    @Test
    fun `processPayment should update status to FAILED on API failure`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "failed@example.com",
            amount = 700,
            currency = Currency.EUR
        )

        val insertedEntities = mutableListOf<TransactionEntity>()
        coEvery { transactionDao.insertTransaction(capture(insertedEntities)) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Failure("Error")

        // When
        repository.processPayment(payment)

        // Then
        insertedEntities.size shouldBe 2
        insertedEntities[0].status shouldBe "PENDING"
        insertedEntities[1].status shouldBe "FAILED"
    }

    @Test
    fun `processPayment should generate unique transaction IDs`() = runTest {
        // Given
        val payment1 = Payment("user1@example.com", 100, Currency.USD)
        val payment2 = Payment("user2@example.com", 200, Currency.EUR)

        val insertedEntities = mutableListOf<TransactionEntity>()
        coEvery { transactionDao.insertTransaction(capture(insertedEntities)) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When
        repository.processPayment(payment1)
        repository.processPayment(payment2)

        // Then
        insertedEntities.size shouldBe 2
        insertedEntities[0].id shouldBe insertedEntities[0].id // IDs should be set
        insertedEntities[1].id shouldBe insertedEntities[1].id
        // IDs should be different (extremely high probability with UUID)
    }

    @Test
    fun `processPayment should send correct DTO to remote API`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "dto@example.com",
            amount = 800,
            currency = Currency.EUR
        )

        val dtoSlot = slot<TransactionDto>()
        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(capture(dtoSlot)) } returns Remote.Success(Unit)

        // When
        repository.processPayment(payment)

        // Then
        dtoSlot.captured.recipient shouldBe "dto@example.com"
        dtoSlot.captured.amount shouldBe 800
        dtoSlot.captured.currency shouldBe "EUR"
        dtoSlot.captured.status shouldBe "PENDING"
    }

    @Test
    fun `processPayment should handle large amounts correctly`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "large@example.com",
            amount = 10000000, // $100,000.00
            currency = Currency.USD
        )

        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<*>>()
        (result as DomainState.Success).data.amount shouldBe 10000000
    }

    @Test
    fun `processPayment should handle small amounts correctly`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "small@example.com",
            amount = 1, // $0.01
            currency = Currency.USD
        )

        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<*>>()
        (result as DomainState.Success).data.amount shouldBe 1
    }

    @Test
    fun `processPayment should work with all supported currencies`() = runTest {
        // Given
        val currencies = listOf(Currency.USD, Currency.EUR)

        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When & Then
        currencies.forEach { currency ->
            val payment = Payment("multi@example.com", 100, currency)
            val result = repository.processPayment(payment)

            result.shouldBeInstanceOf<DomainState.Success<*>>()
            (result as DomainState.Success).data.currency shouldBe currency
        }
    }

    @Test
    fun `processPayment should preserve email with special characters`() = runTest {
        // Given
        val payment = Payment(
            recipientEmail = "user+tag@sub.example.com",
            amount = 900,
            currency = Currency.USD
        )

        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // When
        val result = repository.processPayment(payment)

        // Then
        result.shouldBeInstanceOf<DomainState.Success<*>>()
        (result as DomainState.Success).data.recipient shouldBe "user+tag@sub.example.com"
    }
}
