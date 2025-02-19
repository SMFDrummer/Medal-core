package exception

sealed interface StrategyException {
    object PacketNotFound : StrategyException
    data class TemplateRenderError(val key: String, val cause: Throwable) : StrategyException
    data class JsonModificationError(val path: String, val cause: Throwable) : StrategyException
    data class NetworkError(val cause: Throwable) : StrategyException
    data class DecryptionError(val cause: Throwable) : StrategyException
    data class UnexpectedResponseCode(val expected: Int, val actual: Int) : StrategyException
    data class CredentialExpired(val code: Int) : StrategyException
    data class CredentialRefreshError(val cause: Throwable) : StrategyException
}

class StrategyExceptionWrapper(val exception: StrategyException) : Exception() {
    override val message: String?
        get() = exception.toString()
    override val cause: Throwable?
        get() = (exception as? StrategyException.TemplateRenderError)?.cause
            ?: (exception as? StrategyException.JsonModificationError)?.cause
            ?: (exception as? StrategyException.NetworkError)?.cause
            ?: (exception as? StrategyException.DecryptionError)?.cause
            ?: (exception as? StrategyException.CredentialRefreshError)?.cause
}