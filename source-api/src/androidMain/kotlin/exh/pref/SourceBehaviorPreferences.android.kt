package exh.pref

import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

internal actual fun delegateSourcesEnabled(): Boolean {
    return Injekt.get<DelegateSourcePreferences>().delegateSources().get()
}

internal actual fun japaneseTitlesEnabled(): Boolean {
    return Injekt.get<DelegateSourcePreferences>().useJapaneseTitle().get()
}
