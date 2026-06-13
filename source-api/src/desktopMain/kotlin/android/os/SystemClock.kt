package android.os

@Suppress("UNUSED")
object SystemClock {
    @JvmStatic
    fun elapsedRealtime(): Long = System.nanoTime() / NANOS_PER_MILLISECOND

    @JvmStatic
    fun uptimeMillis(): Long = elapsedRealtime()

    private const val NANOS_PER_MILLISECOND = 1_000_000L
}
