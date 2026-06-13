package tachiyomi.core.platform.javascript

import app.cash.quickjs.QuickJs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AndroidJavaScriptRuntimeFactory : JavaScriptRuntimeFactory {
    override fun create(): JavaScriptRuntime = AndroidQuickJsRuntime()
}

class AndroidQuickJsRuntime : JavaScriptRuntime {
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> evaluate(script: String): T = withContext(Dispatchers.IO) {
        QuickJs.create().use {
            it.evaluate(script) as T
        }
    }
}
