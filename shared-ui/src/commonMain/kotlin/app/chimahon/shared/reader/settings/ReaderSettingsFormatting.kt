package app.chimahon.shared.reader.settings

import kotlin.math.pow
import kotlin.math.round

fun chimahonReaderColorToHex(color: Int): String {
    val hex = (color and 0x00FFFFFF)
        .toString(radix = 16)
        .uppercase()
        .padStart(6, '0')
    return "#$hex"
}

fun parseChimahonReaderColor(value: String): Int? {
    val input = value.trim()
    if (input.isEmpty()) return null
    return parseHexColor(input) ?: parseRgbColor(input)
}

fun formatChimahonReaderNumber(value: Double, suffix: String = "", decimals: Int = 2): String {
    val rounded = roundToDecimal(value, decimals)
    val text = if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        rounded.toString().trimEnd('0').trimEnd('.')
    }
    return text + suffix
}

fun formatChimahonReaderDuration(totalSeconds: Long): String {
    val seconds = totalSeconds.coerceAtLeast(0)
    val remainingSeconds = seconds % 60
    val minutes = (seconds / 60) % 60
    val hours = seconds / 3600
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
    } else {
        "${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
    }
}

fun formatChimahonReaderEta(totalSeconds: Double): String {
    val seconds = totalSeconds.toLong().coerceAtLeast(0L)
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m ${remainingSeconds}s"
        minutes > 0 -> "${minutes}m ${remainingSeconds}s"
        else -> "${remainingSeconds}s"
    }
}

fun chimahonReaderSecondsRemaining(remainingCharacters: Int, speed: Int): Double {
    if (speed <= 0) return 0.0
    return remainingCharacters.coerceAtLeast(0).toDouble() / (speed.toDouble() / 3600.0)
}

internal fun roundToStep(value: Float, step: Double): Double {
    return round(value.toDouble() / step) * step
}

private fun roundToDecimal(value: Double, decimals: Int): Double {
    val factor = 10.0.pow(decimals.coerceAtLeast(0).toDouble())
    return round(value * factor) / factor
}

private fun parseHexColor(input: String): Int? {
    val rawHex = input.removePrefix("#")
    val hex = when (rawHex.length) {
        3 -> rawHex.map { "$it$it" }.joinToString("")
        6, 8 -> rawHex
        else -> return null
    }
    if (!hex.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) return null
    val parsed = hex.toLongOrNull(radix = 16) ?: return null
    return when (hex.length) {
        6 -> (0xFF000000 or parsed).toInt()
        8 -> parsed.toInt()
        else -> null
    }
}

private fun parseRgbColor(input: String): Int? {
    val body = Regex("""rgba?\((.*)\)""", RegexOption.IGNORE_CASE)
        .matchEntire(input)
        ?.groupValues
        ?.get(1)
        ?: input
    val channels = Regex("""\d{1,3}""")
        .findAll(body)
        .mapNotNull { it.value.toIntOrNull() }
        .take(3)
        .toList()
    if (channels.size != 3 || channels.any { it !in 0..255 }) return null
    val (red, green, blue) = channels
    return 0xFF000000.toInt() or (red shl 16) or (green shl 8) or blue
}
