package tachiyomi.core.extensions

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encodeToString
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
            invocationScript(
                script = extension.script,
                sourceId = sourceId,
                method = method,
                arguments = arguments,
            ),
        )
        return json.decodeFromString(deserializer, result)
    }

    private fun invocationScript(
        script: String,
        sourceId: Long,
        method: String,
        arguments: JsonObject,
    ): String {
        val encodedScript = json.encodeToString(script)
        val encodedSourceId = json.encodeToString(sourceId.toString())
        val encodedMethod = json.encodeToString(method)
        val encodedArguments = json.encodeToString(arguments)
        return """
            (function () {
                var module = { exports: {} };
                var exports = module.exports;
                eval($encodedScript);
                var extension = module.exports;
                if (extension && extension.default) {
                    extension = extension.default;
                }
                if ((!extension || Object.keys(extension).length === 0) &&
                    typeof chimahonExtension !== "undefined") {
                    extension = chimahonExtension;
                }
                var source = extension && extension.sources && extension.sources[$encodedSourceId];
                if (!source) {
                    throw new Error("Extension source not found: " + $encodedSourceId);
                }
                var methodName = $encodedMethod;
                var operation = source[methodName];
                if (typeof operation !== "function") {
                    throw new Error("Source method not found: " + methodName);
                }
                var result = operation.call(source, $encodedArguments);
                if (result && typeof result.then === "function") {
                    throw new Error("Async JavaScript methods are not supported; return a request descriptor instead");
                }
                return JSON.stringify(result === undefined ? null : result);
            })();
        """.trimIndent()
    }
}
