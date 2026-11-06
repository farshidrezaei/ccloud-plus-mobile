package com.farshidrezaei.ccloudplus.core.error

sealed class AppError(
    val messagePersian: String,
    val technicalMessage: String? = null,
    val cause: Throwable? = null
) {
    class Network(
        cause: Throwable? = null,
        detail: String? = null
    ) : AppError(
        messagePersian = "خطا در اتصال به اینترنت. لطفاً اتصال شبکه یا دی‌ان‌اس را بررسی کنید.",
        technicalMessage = detail ?: cause?.localizedMessage,
        cause = cause
    )

    class Timeout(
        cause: Throwable? = null
    ) : AppError(
        messagePersian = "پاسخی از سرور دریافت نشد (Timeout). لطفاً اتصال اینترنت خود را بررسی و دوباره تلاش کنید.",
        technicalMessage = cause?.localizedMessage,
        cause = cause
    )

    class SanctionOrDnsBlocked(
        cause: Throwable? = null
    ) : AppError(
        messagePersian = "امکان اتصال به سرور وجود ندارد. احتمال مسدودیت یا تحریم وجود دارد. از تنظیمات برنامه دی‌ان‌اس تحریم‌شکن را فعال نمایید.",
        technicalMessage = cause?.localizedMessage,
        cause = cause
    )

    class Server(
        val statusCode: Int,
        cause: Throwable? = null
    ) : AppError(
        messagePersian = "سرور با کد خطای $statusCode پاسخ داد. لطفاً کمی بعد مجدداً تلاش کنید.",
        technicalMessage = "HTTP $statusCode",
        cause = cause
    )

    class Parse(
        cause: Throwable? = null
    ) : AppError(
        messagePersian = "خطا در پردازش اطلاعات دریافتی از سرور.",
        technicalMessage = cause?.localizedMessage,
        cause = cause
    )

    class Unknown(
        cause: Throwable? = null
    ) : AppError(
        messagePersian = "خطای غیرمنتظره‌ای رخ داده است.",
        technicalMessage = cause?.localizedMessage,
        cause = cause
    )
}

sealed class AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>()
    data class Error(val error: AppError) : AppResult<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
}
