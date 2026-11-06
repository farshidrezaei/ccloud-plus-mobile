package com.farshidrezaei.ccloudplus.core.error

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLException
import org.json.JSONException

object ErrorHandler {
    fun parseException(throwable: Throwable): AppError {
        return when (throwable) {
            is SocketTimeoutException -> AppError.Timeout(throwable)
            is UnknownHostException -> AppError.SanctionOrDnsBlocked(throwable)
            is SSLHandshakeException, is SSLException -> AppError.SanctionOrDnsBlocked(throwable)
            is JSONException -> AppError.Parse(throwable)
            is IOException -> AppError.Network(throwable)
            else -> AppError.Unknown(throwable)
        }
    }
}
