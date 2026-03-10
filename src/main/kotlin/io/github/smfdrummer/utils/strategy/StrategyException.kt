package io.github.smfdrummer.utils.strategy

sealed interface StrategyException {
    object UnknownRetryError : StrategyException
    data class TemplateRenderError(val key: String, val cause: Throwable) : StrategyException
    data class InvalidActionValue(val value: String) : StrategyException
    data class NetworkError(val cause: Throwable) : StrategyException
    data class DecryptionError(val cause: Throwable) : StrategyException
    data class UnexpectedResponseCode(val expect: Int, val actual: Int) : StrategyException
    data class CredentialExpired(val code: Int) : StrategyException
    data class CredentialRefreshError(val cause: Throwable) : StrategyException
}

class StrategyExceptionWrapper(val exception: StrategyException) : Exception() {
    override val message: String?
        get() = exception.toString()
    override val cause: Throwable?
        get() = (exception as? StrategyException.TemplateRenderError)?.cause
            ?: (exception as? StrategyException.NetworkError)?.cause
            ?: (exception as? StrategyException.DecryptionError)?.cause
            ?: (exception as? StrategyException.CredentialRefreshError)?.cause
}