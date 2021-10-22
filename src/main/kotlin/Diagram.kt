import org.jetbrains.skija.*
import kotlin.math.*

// TODO("Area diagram")

/**
 * This component contains [Diagram] classes,
 * and their drawing algorithms.
 */


enum class DiagramType {
    BAR,
    PIE,
    LINE,
}

val DEFAULT_DIAGRAM_TYPE = DiagramType.BAR

/**
 * Possible keywords to specify diagram type.
 */
val diagramTypeByDescription = mapOf(
    "default" to DEFAULT_DIAGRAM_TYPE,
    "bar" to DiagramType.BAR,
    "histogram" to DiagramType.BAR,
    "column" to DiagramType.BAR,
    "pie" to DiagramType.PIE,
    "circle" to DiagramType.PIE,
    "round" to DiagramType.PIE,
    "line" to DiagramType.LINE,
    "graph" to DiagramType.LINE,
)



/* Paints */

private fun fillPaintByColorCode(colorCode: Int) = Paint().apply {
    color = colorCode
    mode = PaintMode.FILL
    strokeWidth = 1f
}

val BLACK_FILL_PAINT = fillPaintByColorCode(0xFF000000.toInt())
val BLACK_STROKE_PAINT = fillPaintByColorCode(0xFF000000.toInt()).apply {
    mode = PaintMode.STROKE
}
val LIGHT_GREY_STROKE_PAINT = fillPaintByColorCode(0xFFAAAAAA.toInt()).apply {
    mode = PaintMode.STROKE
}



/* Fonts */
val TYPEFACE = FontMgr.getDefault().matchFamilyStyle("Lucida Sans Unicode", FontStyle.NORMAL)
val FONT = Font(TYPEFACE, 20f)



fun unionRects(rect1: Rect, rect2: Rect) = Rect(
    min(rect1.left, rect2.left),
    min(rect1.top, rect2.top),
    max(rect1.right, rect2.right),
    max(rect1.bottom, rect2.bottom),
)




/**
 * Generic [Diagram] class.
 *
 * Contains only diagram [data] and some statistics,
 * as well as [draw] function template.
 */
abstract class Diagram(val data: Data) {
    // TODO("Diagram title")
    // TODO("Add selectable options")

    val values = data.map { it.value }
    val labels = data.map { it.label }

    /* Stats */
    val minValue = values.minOf { it }
    val maxValue = values.maxOf { it }
    val sumValues = values
        .sumOf { it.toDouble() }
        .toFloat()


    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0] with size [size].
     *
     * This function should only be called
     * on specific subclasses of [Diagram] class.
     */
    abstract fun draw(canvas: Canvas, x0: Float, y0: Float, size: Float)

    /**
     * Predicts bounding rectangle,
     * if the diagram will be drawn at x0 = y0 = 0 with size [size]
     */
    abstract fun bounds(size: Float): Rect


    /**
     * Returns the PNG data of the diagram.
     */
    fun getPNGData(size: Float): ByteArray? {
        val diagramBounds = bounds(size)

        val bitmap = Bitmap()
        bitmap.imageInfo = ImageInfo(
            diagramBounds.width.toInt() + 2 * PADDING,
            diagramBounds.height.toInt() + 2 * PADDING,
            ColorType.BGRA_8888,
            ColorAlphaType.PREMUL
        )
        bitmap.allocPixels()
        val canvas = Canvas(bitmap)
        draw(canvas, -diagramBounds.left + PADDING, -diagramBounds.top + PADDING, size)
        canvas.readPixels(bitmap, 0, 0)

        val image = Image.makeFromBitmap(bitmap)
        val pngData = image.encodeToData(EncodedImageFormat.PNG) ?: return null
        return pngData.bytes
    }
}




/**
 * Pie diagram.
 *
 * Data with negative values or with zero sum is not allowed.
 */
class PieDiagram(data: Data) : Diagram(data) {

    val FONT_SIZE_COEFFICIENT = 0.05f
    val BLANK_WIDTH_PROPORTION = 0.1f
    val COLOR_BOX_HEIGHT_PROPORTION = 0.7f
    val LABEL_Y_STEP_PROPORTION = 1.3f
    val LABEL_LINE_INDENT = 10f

    val COLOR_PALLET = listOf(
        0xca3f3f,
        0xe0607e,
        0xab92bf,
        0x655a7c,
        0x696d7d,
        0x68b0ab,
        0x8fc0a9,
        0xc8d5b9,
        0xeace71,
        0xa75a39,
    )

    init { // Check for data correctness
        val negativeElement = data.find { it.value < 0 }
        if (negativeElement != null) {
            exitNegativeValues(DiagramType.PIE, negativeElement)
        }

        if (sumValues <= 0f) {
            exitPieZeroSum()
        }
    }


    /**
     * Forms a list of colors for diagram.
     */
    private fun getPaints(records: Int): List<Paint> {
        assert(records > 0)

        var rgbCodes = COLOR_PALLET
        // Ensure that first and last color are not equal
        if (records > rgbCodes.size && records % rgbCodes.size == 1) {
            rgbCodes = rgbCodes.dropLast(1)
        }
        val k = rgbCodes.size / records
        if (k >= 2) { // make colors more distinct
            rgbCodes = rgbCodes.filterIndexed { idx, _ -> idx % k == 0 }
        }
        return rgbCodes.map { fillPaintByColorCode(it or 0xFF000000.toInt()) }
    }



    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0] with size [size].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float, size: Float) {
        val radius = size / 2
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT)
        val maxLabelWidth = labels.maxOf { font.measureTextWidth(it, BLACK_FILL_PAINT) }
        val maxLabelHeight = labels.maxOf { font.measureText(it, BLACK_FILL_PAINT).height }
        val blankWidth = size * BLANK_WIDTH_PROPORTION
        val x1 = x0 + maxLabelWidth + blankWidth
        val y1 = y0
        val xc = x1 + size / 2
        val yc = y1 + size / 2

        val paints = getPaints(data.size)

        // Sectors (arcs) geometry
        val arcLens = values.map { (it * 360f / sumValues) }
        val arcStarts = arcLens.scan(0f) { s, arcLen -> s + arcLen }

        // Draw arcs
        for (i in data.indices) {
            val arcLen = arcLens[i]
            val arcStart = arcStarts[i]
            canvas.drawArc(
                x1,
                y1,
                x1 + size,
                y1 + size,
                arcStart,
                arcLen,
                true,
                paints[i % paints.size],
            )
        }

        // Draw labels
        val colorBoxSz = maxLabelHeight * COLOR_BOX_HEIGHT_PROPORTION
        val colorBoxMarginX = (maxLabelHeight - colorBoxSz) / 2
        val yStep = maxLabelHeight * LABEL_Y_STEP_PROPORTION
        var yCur = y0 + yStep
        for (i in labels.indices) {
            val label = labels[i]
            val labelWidth = font.measureTextWidth(label)
            val labelHeight = font.measureText(label).height

            // Draw color box
            val colorBoxMarginY = (labelHeight - colorBoxSz) / 2
            val colorBoxFill = paints[i % paints.size]
            val colorBox = Rect(
                x0 + colorBoxMarginX,
                yCur - labelHeight + colorBoxMarginY,
                x0 + colorBoxMarginX + colorBoxSz,
                yCur - labelHeight + colorBoxMarginY + colorBoxSz,
            )
            canvas.drawRect(colorBox, colorBoxFill)
            canvas.drawRect(colorBox, BLACK_STROKE_PAINT)

            // Draw lines, if colors coincide
            if (i >= paints.size || i + paints.size < data.size) {
                val arcMid = (arcStarts[i] + arcStarts[i + 1]) / 2
                val arcMidRad = arcMid / 360 * 2 * PI.toFloat()
                val x = xc + radius / 2 * cos(arcMidRad)
                val y = yc + radius / 2 * sin(arcMidRad)
                canvas.drawPolygon(
                    floatArrayOf(
                        x0 + maxLabelHeight + labelWidth + LABEL_LINE_INDENT, yCur - labelHeight / 2,
                        x0 + maxLabelHeight + maxLabelWidth + LABEL_LINE_INDENT, yCur - labelHeight / 2,
                        x, y,
                    ),
                    LIGHT_GREY_STROKE_PAINT,
                )
            }

            // Draw label
            canvas.drawString(label, x0 + maxLabelHeight, yCur, font, BLACK_FILL_PAINT)

            yCur += yStep
        }
    }

    override fun bounds(size: Float): Rect {
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT)
        val maxLabelWidth = labels.maxOf { font.measureTextWidth(it, BLACK_FILL_PAINT) }
        val maxLabelHeight = labels.maxOf { font.measureText(it, BLACK_FILL_PAINT).height }
        val blankWidth = size * BLANK_WIDTH_PROPORTION
        val yStep = maxLabelHeight * LABEL_Y_STEP_PROPORTION

        return Rect(
            0f,
            0f,
            maxLabelWidth + blankWidth + size,
            max(size, yStep * data.size),
        )
    }
}


/**
 * Diagram on a 2D plane:
 * y-axis is for values;
 * x-axis is for labels.
 *
 * [cropBottom] is a flag, which determines
 * whenever the value axis should start from zero (cropBottom = false),
 * or from the smallest value in data (cropBottom = true).
 *
 * Incorporates [BarDiagram] and [LineDiagram].
 */
abstract class PlaneDiagram(data: Data, cropBottom: Boolean): Diagram(data) {

    val FONT_SIZE_COEFFICIENT = 0.03f
    val RULER_LEAK = 5f
    val MARK_LEAK = 3f
    val HORIZONTAL_LABELS_INDENT = 5f

    val rulerStep: Float
    val rulerBegin: Float
    val rulerRangeRel: Int
    val rulerLabels: List<String>

    init {
        /* Value range depending on cropBottom */
        val rangeValues = if (cropBottom || minValue < 0) {
            maxValue - minValue
        } else {
            maxValue
        }

        /* Ruler parameters */
        rulerStep = calcRulerStep(rangeValues)
        val rulerBeginRel = if (cropBottom || minValue < 0) {
            floor(minValue / rulerStep).toInt()
        } else {
            0
        }
        rulerBegin = rulerBeginRel * rulerStep
        val rulerEndRel = floor(maxValue / rulerStep).toInt() + 1
        rulerRangeRel = rulerEndRel - rulerBeginRel

        /* Calc ruler labels */
        val rulerStepIsInteger = floor(rulerStep) == rulerStep
        val decimals = max(0, -floor(log10(rulerStep)).toInt())
        rulerLabels = List(rulerRangeRel + 1) {
            val rulerValue = rulerBegin + it * rulerStep
            if (rulerStepIsInteger)
                rulerValue.toInt().toString()
            else
                "%.${decimals}f".format(rulerValue)
        }
    }



    /**
     * Calculate a somewhat pretty delta value for ruler measuring [range].
     */
    private fun calcRulerStep(range: Float): Float {
        if (range == 0f) {
            // degenerate case
            return 1f
        }
        val k = floor(log10(range)).toInt()
        val d = 10f.pow(k) // the largest power of ten <= range
        return when { // divide if there are too few segments
            2 * d > range -> d / 10f
            3 * d > range -> d / 5f
            4 * d > range -> d / 2f
            else -> d
        }
    }


    /**
     * Get coordinates for value (y) axis on range [y0]..[y1]
     */
    fun getYCoords(y0: Float, y1: Float): List<Float> {
        return data.map { y1 - (it.value - rulerBegin) / (rulerStep * rulerRangeRel) * (y1 - y0) }
    }


    /**
     * Draw ruler on [canvas]
     * with y-range [y0]..[y1],
     * x-range [x0]..[x1],
     * and number labels with [font].
     *
     * One can choose to [drawVerticalLine] or not.
     */
    fun drawRuler(
        canvas: Canvas,
        y0: Float,
        y1: Float,
        x0: Float,
        x1: Float,
        font: Font,
        drawVerticalLine: Boolean = false,
    ) {
        val yStep = (y1 - y0) / rulerRangeRel
        if (drawVerticalLine) {
            canvas.drawLine(x0 - RULER_LEAK, y1, x0 - RULER_LEAK, y0 - RULER_LEAK, LIGHT_GREY_STROKE_PAINT)
        }

        // Draw each of level lines
        for (i in 0..rulerRangeRel) {
            val y = y1 - yStep * i

            // Draw line
            canvas.drawLine(x0 - RULER_LEAK, y, x1 + RULER_LEAK, y, LIGHT_GREY_STROKE_PAINT)

            // Draw label
            val label = rulerLabels[i]
            val labelWidth = font.measureTextWidth(label)
            val labelHeight = font.measureText(label).height
            canvas.drawString(
                label,
                x0 - labelWidth - 2 * RULER_LEAK,
                y + labelHeight / 2,
                font,
                BLACK_FILL_PAINT
            )
        }
    }

    fun rulerBound(size: Float, font: Font): Rect {
        val maxLabelWidth = rulerLabels.maxOf { font.measureTextWidth(it) }
        return Rect(
            -maxLabelWidth - 2 * RULER_LEAK,
            0f,
            0f,
            size + font.measureText(rulerLabels.first()).height
        )
    }

    /**
     * Draw labels on x-axis on [canvas]
     * starting from x=[x0],
     * with x-step [dx],
     * with font [font],
     * on y-level [y].
     *
     * One can choose to [drawMarks] or not (useful only for line diagram so far).
     */
    fun drawHorizontalLabels(
        canvas: Canvas,
        x0: Float,
        dx: Float,
        font: Font,
        y: Float,
        drawMarks: Boolean,
    ) {
        val labelRects = labels.map { label -> font.measureText(label) }
        val labelWidths = labelRects.map { rect -> rect.width }
        val labelHeights = labelRects.map { rect -> rect.height }
        val maxLabelHeight = labelHeights.maxOf { it }

        // Draw each of the labels
        for ((i, label) in labels.withIndex()) {
            val xMid = x0 + dx * i

            // Draw mark
            if (drawMarks) {
                canvas.drawLine(xMid, y + MARK_LEAK, xMid, y - MARK_LEAK, BLACK_FILL_PAINT)
            }

            // Draw label
            canvas.drawString(
                label,
                xMid - labelWidths[i] / 2,
                y + maxLabelHeight + HORIZONTAL_LABELS_INDENT,
                font,
                BLACK_FILL_PAINT
            )
        }
    }

    fun horizontalLabelsBound(size: Float, dx: Float, y: Float, font: Font): Rect {
        val labelRects = labels.map { label -> font.measureText(label) }
        val labelWidths = labelRects.map { rect -> rect.width }
        val labelHeights = labelRects.map { rect -> rect.height }
        val maxLabelHeight = labelHeights.maxOf { it }

        return Rect(
            0f,
            0f,
            dx * labels.size + labelWidths.last(),
            y + maxLabelHeight + HORIZONTAL_LABELS_INDENT,
        )
    }
}


/**
 * Bar diagram.
 *
 * Inherits from [PlaneDiagram].
 *
 * Data with negative values is not allowed.
 */
class BarDiagram(data: Data, cropBottom: Boolean = false) : PlaneDiagram(data, cropBottom) {

    // Blue color for bars
    val BAR_PAINT = fillPaintByColorCode(0xFF4F86C6.toInt())

    val MIN_BAR_WIDTH_COEFFICIENT = 0.12f
    val BAR_PADDING = 2f
    val X_GAP_COEFFICIENT = 0.05f

    init { // Check for data correctness
        val negativeElement = data.find { it.value < 0 }
        if (negativeElement != null) {
            exitNegativeValues(DiagramType.BAR, negativeElement)
        }
    }


    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0] with size [size].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float, size: Float) {
        // Bold labels
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true
        }
        val y1 = y0 + size

        // Prepare geometric values
        val yCoords = getYCoords(y0, y1)
        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val barWidth = max(maxLabelWidth + BAR_PADDING, size * MIN_BAR_WIDTH_COEFFICIENT)
        val xGap = size * X_GAP_COEFFICIENT
        val xStep = barWidth + xGap
        val x2 = x0 + xStep * (data.size - 1) + barWidth

        // Draw bars
        for (i in data.indices) {
            val x = x0 + xStep * i
            val y = yCoords[i]
            canvas.drawRect(Rect(x, y, x + barWidth, y1), BAR_PAINT)
        }

        drawHorizontalLabels(
            canvas,
            x0 + barWidth / 2,
            xStep,
            font,
            y1,
            false,
        )

        drawRuler(
            canvas,
            y0,
            y1,
            x0,
            x2,
            font,
        )
    }

    override fun bounds(size: Float): Rect {
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true
        }

        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val barWidth = max(maxLabelWidth + BAR_PADDING, size * MIN_BAR_WIDTH_COEFFICIENT)
        val xGap = size * X_GAP_COEFFICIENT
        val xStep = barWidth + xGap

        val rulerBound = rulerBound(size, font)
        val horizontalLabelsBound = horizontalLabelsBound(size, xStep, size, font)
        return unionRects(rulerBound, horizontalLabelsBound)
    }
}

class LineDiagram(data: Data, cropBottom: Boolean = true) : PlaneDiagram(data, cropBottom) {

    val GRAPH_COLOR_CODE = 0xFF4F86C6.toInt()

    val LINE_STROKE_WIDTH_COEFFICIENT = 0.005f
    val POINT_STROKE_WIDTH_COEFFICIENT = 0.012f
    val MIN_X_MARGIN_COEFFICIENT = 0.05f
    val MIN_X_STEP_COEFFICIENT = 0.15f
    val X_STEP_INDENT_COEFFICIENT = 0.035f

    /**
     * Draws diagram on [canvas] at top-left point [x0], [y0] with size [size].
     */
    override fun draw(canvas: Canvas, x0: Float, y0: Float, size: Float) {
        // Bold labels
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true
        }
        val linePaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = LINE_STROKE_WIDTH_COEFFICIENT * size
        }
        val pointPaint = fillPaintByColorCode(GRAPH_COLOR_CODE).apply {
            strokeWidth = POINT_STROKE_WIDTH_COEFFICIENT * size
        }

        // Prepare geometric values
        val y1 = y0 + size
        val yCoords = getYCoords(y0, y1)
        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val xMargin = max(maxLabelWidth / 2, size * MIN_X_MARGIN_COEFFICIENT)
        val x1 = x0 + xMargin
        val xStep = max(maxLabelWidth + size * X_STEP_INDENT_COEFFICIENT, size * MIN_X_STEP_COEFFICIENT)
        val points = data.mapIndexed { i, _ ->
            Pair(x1 + xStep * i, yCoords[i])
        }
        val pointsFlatten = points
            .flatMap { (x, y) -> listOf(x, y) }
            .toFloatArray()

        drawHorizontalLabels(
            canvas,
            x1,
            xStep,
            font,
            y1,
            true,
        )

        // Draw lines and points
        canvas.drawPolygon(pointsFlatten, linePaint)
        points.forEach { (x, y) ->
            canvas.drawPoint(x, y, pointPaint)
        }

        drawRuler(
            canvas,
            y0,
            y1,
            x0,
            x1 + xStep * (data.size - 1) + xMargin,
            font,
            true,
        )
    }

    override fun bounds(size: Float): Rect {
        val font = FONT.makeWithSize(size * FONT_SIZE_COEFFICIENT).apply {
            isEmboldened = true
        }

        val maxLabelWidth = labels.maxOf { label ->
            font.measureTextWidth(label)
        }
        val xStep = max(maxLabelWidth + size * X_STEP_INDENT_COEFFICIENT, size * MIN_X_STEP_COEFFICIENT)

        val rulerBound = rulerBound(size, font)
        val horizontalLabelsBound = horizontalLabelsBound(size, xStep, size, font)
        return unionRects(rulerBound, horizontalLabelsBound)
    }
}