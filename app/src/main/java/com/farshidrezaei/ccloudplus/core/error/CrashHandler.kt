package com.farshidrezaei.ccloudplus.core.error

import android.content.Context
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandler private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            saveCrashLog(thread, throwable)
        } catch (e: Exception) {
            Log.e("CrashHandler", "Failed to write crash log", e)
        } finally {
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun saveCrashLog(thread: Thread, throwable: Throwable) {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        val stackTrace = stringWriter.toString()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val logContent = buildString {
            append("Timestamp: $timestamp\n")
            append("Thread: ${thread.name} (${thread.id})\n")
            append("Exception: ${throwable.javaClass.name}\n")
            append("Message: ${throwable.message}\n")
            append("Stack trace:\n")
            append(stackTrace)
            append("\n----------------------------------------\n\n")
        }

        val logFile = File(context.filesDir, "crash_log.txt")
        logFile.appendText(logContent)
        Log.e("CCloudPlus_CRASH", logContent)
    }

    companion object {
        fun install(context: Context) {
            CrashHandler(context.applicationContext)
        }
    }
}
