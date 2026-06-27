package tachiyomi.core.extensions

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import tachiyomi.core.platform.javascript.JavaScriptRuntimeFactory

class ScriptExtensionInvoker(
    private val runtimeFactory: JavaScriptRuntimeFactory,
    private val json: Json = Json {
        ignoreUnknownKeys = true
    },
) {
    suspend fun <T> invoke(
        extension: LoadedScriptExtension,
        sourceId: Long,
        method: String,
        arguments: JsonObject,
        deserializer: DeserializationStrategy<T>,
    ): T {
        val result = runtimeFactory.create().evaluate<String>(
            ScriptExtensionJavaScriptBridge.invocationScript(
                script = extension.script,
                sourceId = sourceId,
                method = method,
                arguments = arguments,
                json = json,
            ),
        )
        return json.decodeFromString(deserializer, result)
    }
}
