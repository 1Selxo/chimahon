package eu.kanade.tachiyomi.source

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Base64
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

actual interface PlatformSharedPreferences {
    fun getAll(): Map<String, *>
    fun getString(key: String, defValue: String?): String?
    fun getStringSet(key: String, defValues: Set<String>?): Set<String>?
    fun getInt(key: String, defValue: Int): Int
    fun getLong(key: String, defValue: Long): Long
    fun getFloat(key: String, defValue: Float): Float
    fun getBoolean(key: String, defValue: Boolean): Boolean
    fun contains(key: String): Boolean
    fun edit(): Editor

    interface Editor {
        fun putString(key: String, value: String?): Editor
        fun putStringSet(key: String, values: Set<String>?): Editor
        fun putInt(key: String, value: Int): Editor
        fun putLong(key: String, value: Long): Editor
        fun putFloat(key: String, value: Float): Editor
        fun putBoolean(key: String, value: Boolean): Editor
        fun remove(key: String): Editor
        fun clear(): Editor
        fun commit(): Boolean
        fun apply()
    }
}

private val desktopSourcePreferences = ConcurrentHashMap<String, DesktopPlatformSharedPreferences>()

actual fun platformSourcePreferences(key: String): PlatformSharedPreferences {
    return desktopSourcePreferences.getOrPut(key) {
        DesktopPlatformSharedPreferences(desktopSourcePreferencesFile(key))
    }
}

private class DesktopPlatformSharedPreferences(
    private val file: Path,
) : PlatformSharedPreferences {
    private val lock = Any()
    private var values: Map<String, String> = loadValues()

    override fun getAll(): Map<String, *> = synchronized(lock) {
        values.mapValues { (_, encoded) -> encoded.decodePreferenceValue() }
    }

    override fun getString(key: String, defValue: String?): String? = synchronized(lock) {
        values[key]?.decodePreferenceValue() as? String ?: defValue
    }

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? = synchronized(lock) {
        values[key]?.decodePreferenceValue() as? Set<String> ?: defValues
    }

    override fun getInt(key: String, defValue: Int): Int = synchronized(lock) {
        values[key]?.decodePreferenceValue() as? Int ?: defValue
    }

    override fun getLong(key: String, defValue: Long): Long = synchronized(lock) {
        values[key]?.decodePreferenceValue() as? Long ?: defValue
    }

    override fun getFloat(key: String, defValue: Float): Float = synchronized(lock) {
        values[key]?.decodePreferenceValue() as? Float ?: defValue
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean = synchronized(lock) {
        values[key]?.decodePreferenceValue() as? Boolean ?: defValue
    }

    override fun contains(key: String): Boolean = synchronized(lock) {
        key in values
    }

    override fun edit(): PlatformSharedPreferences.Editor = Editor()

    private fun loadValues(): Map<String, String> {
        if (!file.exists()) return emptyMap()
        val properties = Properties()
        return runCatching {
            file.inputStream().use(properties::load)
            properties.entries.associate { (key, value) -> key.toString() to value.toString() }
        }.getOrDefault(emptyMap())
    }

    private fun writeValues(nextValues: Map<String, String>) {
        file.parent?.createDirectories()
        val properties = Properties().apply {
            nextValues.forEach { (key, value) -> setProperty(key, value) }
        }
        val tempFile = Files.createTempFile(file.parent, file.fileName.toString(), ".tmp")
        tempFile.outputStream().use { output ->
            properties.store(output, "Chimahon desktop source preferences")
        }
        runCatching {
            Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        }.getOrElse {
            Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private inner class Editor : PlatformSharedPreferences.Editor {
        private val pending = linkedMapOf<String, String?>()
        private var clearRequested = false

        override fun putString(key: String, value: String?): PlatformSharedPreferences.Editor = apply {
            pending[key] = value?.encodePreferenceValue()
        }

        override fun putStringSet(key: String, values: Set<String>?): PlatformSharedPreferences.Editor = apply {
            pending[key] = values?.encodePreferenceValue()
        }

        override fun putInt(key: String, value: Int): PlatformSharedPreferences.Editor = apply {
            pending[key] = value.encodePreferenceValue()
        }

        override fun putLong(key: String, value: Long): PlatformSharedPreferences.Editor = apply {
            pending[key] = value.encodePreferenceValue()
        }

        override fun putFloat(key: String, value: Float): PlatformSharedPreferences.Editor = apply {
            pending[key] = value.encodePreferenceValue()
        }

        override fun putBoolean(key: String, value: Boolean): PlatformSharedPreferences.Editor = apply {
            pending[key] = value.encodePreferenceValue()
        }

        override fun remove(key: String): PlatformSharedPreferences.Editor = apply {
            pending[key] = null
        }

        override fun clear(): PlatformSharedPreferences.Editor = apply {
            clearRequested = true
        }

        override fun commit(): Boolean {
            return runCatching {
                synchronized(lock) {
                    val nextValues = if (clearRequested) {
                        linkedMapOf()
                    } else {
                        values.toMutableMap()
                    }
                    pending.forEach { (key, value) ->
                        if (value == null) {
                            nextValues.remove(key)
                        } else {
                            nextValues[key] = value
                        }
                    }
                    writeValues(nextValues)
                    values = nextValues.toMap()
                }
            }.isSuccess
        }

        override fun apply() {
            commit()
        }
    }
}

private fun desktopSourcePreferencesFile(key: String): Path {
    val safeKey = key
        .ifBlank { "default" }
        .map { character ->
            if (character.isLetterOrDigit() || character == '-' || character == '_') character else '_'
        }
        .joinToString("")
        .take(96)
        .ifBlank { "default" }
    return Path.of(
        System.getProperty("user.home"),
        ".chimahon",
        "source-preferences",
        "$safeKey.properties",
    )
}

private fun String.decodePreferenceValue(): Any? {
    val separatorIndex = indexOf(':')
    if (separatorIndex <= 0) return this
    val type = take(separatorIndex)
    val payload = drop(separatorIndex + 1)
    return when (type) {
        "s" -> payload.decodeBase64Text()
        "set" -> if (payload.isBlank()) {
            emptySet<String>()
        } else {
            payload.split(',').mapTo(linkedSetOf()) { it.decodeBase64Text() }
        }
        "i" -> payload.toIntOrNull()
        "l" -> payload.toLongOrNull()
        "f" -> payload.toFloatOrNull()
        "b" -> payload.toBooleanStrictOrNull()
        else -> this
    }
}

private fun String.encodePreferenceValue(): String = "s:${encodeBase64Text()}"

private fun Set<String>.encodePreferenceValue(): String {
    return joinToString(separator = ",", prefix = "set:") { it.encodeBase64Text() }
}

private fun Int.encodePreferenceValue(): String = "i:$this"

private fun Long.encodePreferenceValue(): String = "l:$this"

private fun Float.encodePreferenceValue(): String = "f:$this"

private fun Boolean.encodePreferenceValue(): String = "b:$this"

private fun String.encodeBase64Text(): String {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(encodeToByteArray())
}

private fun String.decodeBase64Text(): String {
    return String(Base64.getUrlDecoder().decode(this))
}
