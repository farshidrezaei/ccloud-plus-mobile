package com.farshidrezaei.ccloudplus.core.error

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLException
import org.json.JSONException

object ErrorHandler {
    fun parseException(throwable: Throwable): AppError {
        val rootCause = findRootCause(throwable)
        return when {
            throwable is AppError -> throwable
            rootCause is SocketTimeoutException || throwable is SocketTimeoutException -> AppError.Timeout(throwable)
            rootCause is UnknownHostException || throwable is UnknownHostException -> AppError.SanctionOrDnsBlocked(throwable)
            rootCause is SSLHandshakeException || throwable is SSLHandshakeException ||
            rootCause is SSLException || throwable is SSLException -> AppError.SanctionOrDnsBlocked(throwable)
            rootCause is JSONException || throwable is JSONException -> AppError.Parse(throwable)
            rootCause is IOException || throwable is IOException -> AppError.Network(throwable)
            else -> AppError.Unknown(throwable)
        }
    }

    private fun findRootCause(throwable: Throwable): Throwable {
        var cause: Throwable = throwable
        while (cause.cause != null && cause.cause !== cause) {
            cause = cause.cause!!
        }
        return cause
    }
}
