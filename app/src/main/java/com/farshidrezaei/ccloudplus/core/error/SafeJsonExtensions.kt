package com.farshidrezaei.ccloudplus.core.error

import org.json.JSONArray
import org.json.JSONObject

/**
 * Safe extensions for JSONObject and JSONArray to prevent runtime JSONExceptions
 * when external streaming APIs return null, mistyped, or missing fields.
 */

fun JSONObject.optStringSafe(key: String, default: String = ""): String {
    if (!has(key) || isNull(key)) return default
    return try {
        optString(key, default).trim()
    } catch (e: Exception) {
        default
    }
}

fun JSONObject.optIntSafe(key: String, default: Int = 0): Int {
    if (!has(key) || isNull(key)) return default
    return try {
        optInt(key, default)
    } catch (e: Exception) {
        default
    }
}

fun JSONObject.optDoubleSafe(key: String, default: Double = 0.0): Double {
    if (!has(key) || isNull(key)) return default
    return try {
        optDouble(key, default)
    } catch (e: Exception) {
        default
    }
}

fun JSONObject.optBooleanSafe(key: String, default: Boolean = false): Boolean {
    if (!has(key) || isNull(key)) return default
    return try {
        optBoolean(key, default)
    } catch (e: Exception) {
        default
    }
}

fun JSONObject.optArraySafe(key: String): JSONArray {
    if (!has(key) || isNull(key)) return JSONArray()
    return try {
        optJSONArray(key) ?: JSONArray()
    } catch (e: Exception) {
        JSONArray()
    }
}

fun JSONObject.optObjectSafe(key: String): JSONObject? {
    if (!has(key) || isNull(key)) return null
    return try {
        optJSONObject(key)
    } catch (e: Exception) {
        null
    }
}

inline fun <T> JSONArray.mapNotNullSafe(transform: (JSONObject) -> T?): List<T> {
    val list = mutableListOf<T>()
    for (i in 0 until length()) {
        try {
            val item = optJSONObject(i)
            if (item != null) {
                transform(item)?.let { list.add(it) }
            }
        } catch (ignored: Exception) {
            // Defensively skip corrupted individual items
        }
    }
    return list
}
