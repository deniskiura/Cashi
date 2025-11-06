package ke.kiura.cashi.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Currency(val code: String, val symbol: String) {
    USD("USD", "$"),
    EUR("EUR", "€");

    companion object {
        fun fromCode(code: String): Currency? {
            return entries.find { it.code.equals(code, ignoreCase = true) }
        }
    }
}
