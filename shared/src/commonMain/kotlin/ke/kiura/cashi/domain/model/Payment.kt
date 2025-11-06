package ke.kiura.cashi.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a payment request
 * This is the core business entity used throughout the application
 */
@Serializable
data class Payment(
    val recipientEmail: String,
    val amount: Int,
    val currency: Currency
) {
    /**
     * Validates the payment data
     * @return ValidationResult indicating if payment is valid or error reason
     */
    fun validate(): ValidationResult {
        return when {
            !isValidEmail(recipientEmail) -> ValidationResult.Invalid("Invalid email format")
            amount <= 0 -> ValidationResult.Invalid("Amount must be greater than zero")
            else -> ValidationResult.Valid
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return email.matches(emailRegex)
    }
}

/**
 * Result of payment validation
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()

    fun isValid(): Boolean = this is Valid
    fun errorMessage(): String? = (this as? Invalid)?.reason
}
