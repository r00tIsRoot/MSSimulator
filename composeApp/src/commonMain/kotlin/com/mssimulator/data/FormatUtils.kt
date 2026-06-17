package com.mssimulator.data

/**
 * String.format() is not available on wasmJs target in Kotlin 2.1.x.
 * This utility provides the format patterns we need.
 */

fun formatLargeNumber(value: Double): String {
    return when {
        value >= 1_000_000_000_000 -> formatDecimal(value / 1_000_000_000_000, 2) + "조"
        value >= 100_000_000 -> formatDecimal(value / 100_000_000, 1) + "억"
        value >= 1_000_000_000 -> formatDecimal(value / 1_000_000_000, 2) + "B"
        value >= 1_000_000 -> formatDecimal(value / 1_000_000, 2) + "M"
        value >= 1_000 -> formatDecimal(value / 1_000, 2) + "K"
        else -> formatDecimal(value, 0)
    }
}

fun formatTime(seconds: Double): String {
    val totalSec = seconds.toInt().coerceAtLeast(0)
    val m = totalSec / 60
    val s = totalSec % 60
    return "${m}분 ${s}초"
}

fun formatAxisLabel(value: Double): String {
    return when {
        value >= 1_000_000_000_000 -> formatDecimal(value / 1_000_000_000_000, 1) + "조"
        value >= 100_000_000 -> formatDecimal(value / 100_000_000, 0) + "억"
        value >= 1_000_000 -> formatDecimal(value / 1_000_000, 0) + "M"
        value >= 1_000 -> formatDecimal(value / 1_000, 0) + "K"
        else -> formatDecimal(value, 0)
    }
}

/** Round to N decimal places and return string. N must be 0-10. */
fun formatDecimal(value: Double, decimals: Int): String {
    if (value.isNaN() || value.isInfinite()) return "0"
    if (decimals <= 0) {
        return (value + 0.5).toLong().toString()
    }
    val factor = (0 until decimals).fold(1L) { acc, _ -> acc * 10L }
    val rounded = (value * factor + 0.5).toLong()
    val intPart = rounded / factor
    val decPart = (rounded % factor).toInt()
    val decStr = decPart.toString().padStart(decimals, '0')
    return "$intPart.$decStr"
}
