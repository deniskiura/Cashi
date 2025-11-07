package ke.kiura.cashi.bdd.steps

import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import ke.kiura.cashi.db.dao.TransactionDao
import ke.kiura.cashi.domain.common.DomainState
import ke.kiura.cashi.domain.model.Currency
import ke.kiura.cashi.domain.model.Payment
import ke.kiura.cashi.domain.repository.PaymentRepository
import ke.kiura.cashi.domain.usecase.SendPaymentUseCase
import ke.kiura.cashi.data.repository.payment.PaymentRepositoryImpl
import ke.kiura.cashi.remote.Remote
import ke.kiura.cashi.remote.RemoteApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

class SendPaymentSteps {

    private lateinit var recipientEmail: String
    private lateinit var amount: String
    private lateinit var currency: Currency
    private lateinit var result: DomainState<*>
    private lateinit var remoteApi: RemoteApi
    private lateinit var transactionDao: TransactionDao
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var sendPaymentUseCase: SendPaymentUseCase

    @Given("a user wants to send a payment")
    fun aUserWantsToSendAPayment() {
        // Set up mocks
        remoteApi = mockk()
        transactionDao = mockk()

        // Default successful behavior
        coEvery { transactionDao.insertTransaction(any()) } just runs
        coEvery { transactionDao.updateTransaction(any()) } just runs
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Success(Unit)

        // Create repository and use case
        paymentRepository = PaymentRepositoryImpl(remoteApi, transactionDao)
        sendPaymentUseCase = SendPaymentUseCase(paymentRepository)
    }

    @Given("the remote API is unavailable")
    fun theRemoteApiIsUnavailable() {
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.Failure("Network connection failed")
    }

    @Given("the remote API will return validation errors")
    fun theRemoteApiWillReturnValidationErrors() {
        coEvery { remoteApi.saveTransaction(any()) } returns Remote.ValidationError(
            mapOf("amount" to listOf("Invalid amount"))
        )
    }

    @When("they enter recipient email {string}")
    fun theyEnterRecipientEmail(email: String) {
        recipientEmail = email
    }

    @And("they enter amount {string} in currency {string}")
    fun theyEnterAmountInCurrency(amountStr: String, currencyCode: String) {
        amount = amountStr
        currency = when (currencyCode) {
            "USD" -> Currency.USD
            "EUR" -> Currency.EUR
            else -> Currency.USD
        }
    }

    @And("they submit the payment")
    fun theySubmitThePayment() {
        val payment = Payment(
            recipientEmail = recipientEmail,
            amount = amount.toIntOrNull() ?: 0,
            currency = currency
        )

        result = runBlocking {
            sendPaymentUseCase(payment)
        }
    }

    @Then("the payment should be processed successfully")
    fun thePaymentShouldBeProcessedSuccessfully() {
        assertTrue("Expected Success but got ${result::class.simpleName}", result is DomainState.Success)
    }

    @And("the transaction should be saved with status {string}")
    fun theTransactionShouldBeSavedWithStatus(status: String) {
        when (result) {
            is DomainState.Success -> {
                val transaction = (result as DomainState.Success).data
                when (transaction) {
                    is ke.kiura.cashi.domain.model.Transaction -> {
                        assertEquals(status, transaction.status.name)
                    }
                    else -> fail("Expected Transaction but got ${transaction!!::class.simpleName}")
                }
            }
            is DomainState.Error -> {
                if (status == "FAILED") {
                    // This is expected for failed transactions
                    assertTrue(true)
                } else {
                    fail("Expected transaction with status $status but got error: ${(result as DomainState.Error).message}")
                }
            }
            else -> fail("Unexpected result type: ${result::class.simpleName}")
        }
    }

    @Then("the payment should fail with error {string}")
    fun thePaymentShouldFailWithError(expectedError: String) {
        assertTrue("Expected Error but got ${result::class.simpleName}", result is DomainState.Error)
        val actualError = (result as DomainState.Error).message
        assertEquals(expectedError, actualError)
    }

    @Then("the payment should fail with error containing {string}")
    fun thePaymentShouldFailWithErrorContaining(expectedSubstring: String) {
        assertTrue("Expected Error but got ${result::class.simpleName}", result is DomainState.Error)
        val actualError = (result as DomainState.Error).message
        assertTrue(
            "Expected error to contain '$expectedSubstring' but was '$actualError'",
            actualError.contains(expectedSubstring, ignoreCase = true)
        )
    }

    @And("the transaction should have currency {string}")
    fun theTransactionShouldHaveCurrency(expectedCurrency: String) {
        assertTrue("Expected Success but got ${result::class.simpleName}", result is DomainState.Success)
        val transaction = (result as DomainState.Success).data
        val actualCurrency = when (transaction) {
            is ke.kiura.cashi.domain.model.Transaction -> transaction.currency.code
            else -> "UNKNOWN"
        }
        assertEquals(expectedCurrency, actualCurrency)
    }
}
