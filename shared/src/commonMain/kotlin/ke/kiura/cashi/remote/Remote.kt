package ke.kiura.cashi.remote

sealed class Remote<out T> {
    data class Success<out T>(val data: T) : Remote<T>()
    data class Failure(val error: String) : Remote<Nothing>()
    data class ValidationError(
        val errors: Map<String, List<String>>
    ) : Remote<Nothing>()

    data object UnAuthenticated : Remote<Nothing>()
}