package tachiyomi.core.platform.javascript

interface JavaScriptRuntime {
    suspend fun <T> evaluate(script: String): T
}

fun interface JavaScriptRuntimeFactory {
    fun create(): JavaScriptRuntime
}

class UnsupportedJavaScriptRuntime(
    private val platformName: String,
) : JavaScriptRuntime {
    override suspend fun <T> evaluate(script: String): T {
        error("JavaScript execution is not implemented for $platformName yet")
    }
}
