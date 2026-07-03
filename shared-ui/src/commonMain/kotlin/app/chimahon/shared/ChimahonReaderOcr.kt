package app.chimahon.shared

data class ChimahonReaderOcrBlock(
    val xmin: Float,
    val ymin: Float,
    val xmax: Float,
    val ymax: Float,
    val lines: List<String>,
    val vertical: Boolean = false,
    val lineGeometries: List<ChimahonReaderOcrLineGeometry>? = null,
    val language: String = "",
)

data class ChimahonReaderOcrLineGeometry(
    val xmin: Float,
    val ymin: Float,
    val xmax: Float,
    val ymax: Float,
    val rotation: Float = 0f,
)
