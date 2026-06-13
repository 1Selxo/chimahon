package eu.kanade.tachiyomi.source

import android.app.Application
import android.content.Context
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

actual typealias PlatformSharedPreferences = android.content.SharedPreferences

actual fun platformSourcePreferences(key: String): PlatformSharedPreferences {
    return Injekt.get<Application>().getSharedPreferences(key, Context.MODE_PRIVATE)
}
