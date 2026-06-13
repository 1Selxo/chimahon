package tachiyomi.core.platform.settings

interface PlatformSettingsStore {
    suspend fun readString(key: String): String?

    suspend fun writeString(key: String, value: String)

    suspend fun remove(key: String)

    suspend fun clear()

    suspend fun snapshot(): Map<String, String>
}

suspend fun PlatformSettingsStore.readString(
    key: String,
    defaultValue: String,
): String {
    return readString(key) ?: defaultValue
}

suspend fun PlatformSettingsStore.readBoolean(
    key: String,
    defaultValue: Boolean = false,
): Boolean {
    return readString(key)?.toBooleanStrictOrNull() ?: defaultValue
}

suspend fun PlatformSettingsStore.writeBoolean(
    key: String,
    value: Boolean,
) {
    writeString(key, value.toString())
}

suspend fun PlatformSettingsStore.readInt(
    key: String,
    defaultValue: Int = 0,
): Int {
    return readString(key)?.toIntOrNull() ?: defaultValue
}

suspend fun PlatformSettingsStore.writeInt(
    key: String,
    value: Int,
) {
    writeString(key, value.toString())
}
