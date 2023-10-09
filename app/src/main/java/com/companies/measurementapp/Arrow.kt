package com.companies.measurementapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MagnifierStyle
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArrowDrawing(viewModel: MeasureViewModel) {
    val lines = viewModel.lines
    var currentLine by viewModel.currentLine

    val textMeasurer = rememberTextMeasurer()
    val textSpacing = (-15).dp

    var magnifierCenter by remember { mutableStateOf(Offset.Unspecified) }
    var magnifierEnd by remember { mutableStateOf(Offset.Unspecified) }


    val configuration = LocalConfiguration.current

    val screenSize = LocalDensity.current.run {
        DpSize(
            width = with(LocalDensity.current) { configuration.screenWidthDp.dp },
            height = with(LocalDensity.current) { configuration.screenHeightDp.dp }
        )
    }
    val topMargin = 50.dp
    val bottomMargin = 220.dp

    val widgetCenter = with(LocalDensity.current) {
        Offset(
            x = screenSize.width.toPx() - topMargin.toPx(),
            y = topMargin.toPx()
        )
    }
    val widgetCenterBottom = with(LocalDensity.current) {
        Offset(
            x = screenSize.width.toPx() - topMargin.toPx(),
            y = screenSize.height.toPx() - bottomMargin.toPx()
        )
    }

    var isEditMode by viewModel.isEditMode

    var isWholeLineDrag by remember {
        mutableStateOf(false)
    }
    
    Canvas(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        magnifierCenter = Offset.Unspecified
                        magnifierEnd = Offset.Unspecified
                        lines.forEach { line ->
                            line.isDraggingStartPoint = false
                            line.isDraggingEndPoint = false
                            line.isDraggingWholeLine = false
                        }
                        isEditMode = false
                        currentLine?.let {
                            viewModel.addIntoList(it)
                            viewModel.updateCurrentLine(it)
                        }
                        currentLine = null

                    },
                    onDragStart = { offset ->
                        val tolerance = 16.dp.toPx()
                        val linesCopy = lines.toList()

                        val lineToEdit = linesCopy.find { line ->
                            val startPoint = line.start
                            val endPoint = line.end
                            val startX = min(startPoint.x, endPoint.x)
                            val startY = min(startPoint.y, endPoint.y)
                            val endX = max(startPoint.x, endPoint.x)
                            val endY = max(startPoint.y, endPoint.y)

                            offset.x >= startX - tolerance && offset.x <= endX + tolerance &&
                                    offset.y >= startY - tolerance && offset.y <= endY + tolerance
                        }


                        if (lineToEdit != null) {
                            if (offset.x >= lineToEdit.start.x - tolerance && offset.x <= lineToEdit.start.x + tolerance &&
                                offset.y >= lineToEdit.start.y - tolerance && offset.y <= lineToEdit.start.y + tolerance
                            ) {
                                lineToEdit.isDraggingStartPoint = true
                                lineToEdit.isDraggingEndPoint = false
                                isEditMode = true
                            } else if (offset.x >= lineToEdit.end.x - tolerance && offset.x <= lineToEdit.end.x + tolerance &&
                                offset.y >= lineToEdit.end.y - tolerance && offset.y <= lineToEdit.end.y + tolerance
                            ) {
                                lineToEdit.isDraggingStartPoint = false
                                lineToEdit.isDraggingEndPoint = true
                                isEditMode = true
                            } else {
                                lineToEdit.isDraggingStartPoint = false
                                lineToEdit.isDraggingEndPoint = false
                                lineToEdit.isDraggingWholeLine = true
                                isWholeLineDrag = true
                                isEditMode = true
                            }
                        }

                    }
                ) { change, dragAmount ->
                    change.consume()
                    val linesCopy = lines.toList()
                    if (isEditMode) {
                        linesCopy.forEach { lineToEdit ->
                            if (lineToEdit.isDraggingStartPoint) {
                                val updatedLine = lineToEdit.copy(
                                    start = lineToEdit.start + dragAmount
                                )
                                viewModel.updateLineById(lineToEdit.id, updatedLine)
                                magnifierCenter = updatedLine.start
                            } else if (lineToEdit.isDraggingEndPoint) {
                                val updatedLine = lineToEdit.copy(
                                    end = lineToEdit.end + dragAmount
                                )
                                viewModel.updateLineById(lineToEdit.id, updatedLine)
                                magnifierCenter = updatedLine.end
                            } else if (lineToEdit.isDraggingWholeLine) {
                                val updatedLine = lineToEdit.copy(
                                    start = lineToEdit.start + dragAmount,
                                    end = lineToEdit.end + dragAmount
                                )
                                magnifierCenter = updatedLine.start
                                magnifierEnd = updatedLine.end
                                viewModel.updateLineById(lineToEdit.id, updatedLine)
                            }
                        }
                    } else {
                        val updatedLine =
                            currentLine?.copy(end = currentLine!!.end + dragAmount)
                        if (updatedLine != null) {
                            viewModel.updateCurrentLine(updatedLine)
                            magnifierCenter = updatedLine.end
                        } else {
                            val line = Line(
                                start = change.position,
                                end = change.position + dragAmount,
                                strokeWidth = 5.dp,
                                text = currentLine?.text ?: "?",
                                color = currentLine?.color ?: Color.Blue
                            )
                            viewModel.updateCurrentLine(line)
                        }

                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    val tappedLine = lines.find { line ->
                        val tolerance = 16.dp.toPx()
                        val startX = min(line.start.x, line.end.x)
                        val endX = max(line.start.x, line.end.x)
                        val startY = min(line.start.y, line.end.y)
                        val endY = max(line.start.y, line.end.y)
                        offset.x >= startX - tolerance && offset.x <= endX + tolerance &&
                                offset.y >= startY - tolerance && offset.y <= endY + tolerance
                    }
                    if (tappedLine != null) {
                        viewModel.editLineById(tappedLine.id)
                        viewModel.updateCurrentLine(tappedLine)
                    }
                })
            }
            .fillMaxSize()
            .magnifier(
                sourceCenter = { magnifierCenter },
                magnifierCenter = { widgetCenter },
                zoom = 1.7f,
                style = MagnifierStyle(
                    size = DpSize(width = 110.dp, height = 110.dp),
                    cornerRadius = 100.dp,
                    elevation = 5.dp
                )
            )
            .then(
                if (isWholeLineDrag) {
                    Modifier.magnifier(
                        sourceCenter = { magnifierEnd },
                        magnifierCenter = { widgetCenterBottom },
                        zoom = 1.7f,
                        style = MagnifierStyle(
                            size = DpSize(width = 110.dp, height = 110.dp),
                            cornerRadius = 100.dp,
                            elevation = 5.dp
                        )
                    )
                } else {
                    Modifier
                }
            )
    ) {

        lines.forEach { line ->
            val textLayoutResult: TextLayoutResult =
                textMeasurer.measure(
                    text = line.text,
                    style = TextStyle(color = line.color, fontSize = 15.sp),
                    layoutDirection = LayoutDirection.Ltr
                )
            val gapSize = textLayoutResult.size.width.dp
            val isVerticalLine = abs(line.end.y - line.start.y) > abs(line.end.x - line.start.x)

            drawArrowheads(
                start = line.start,
                end = line.end,
                color = line.color,
                size = line.arrowHeadSize
            )

            val centerX = (line.start.x + line.end.x) / 2
            val centerY = (line.start.y + line.end.y) / 2

            val angle = atan2(line.end.y - line.start.y, line.end.x - line.start.x)

            val gapStart = Offset(
                centerX - gapSize.toPx() / 2 * cos(angle),
                centerY - gapSize.toPx() / 2 * sin(angle)
            )
            val gapEnd = Offset(
                centerX + gapSize.toPx() / 2 * cos(angle),
                centerY + gapSize.toPx() / 2 * sin(angle)
            )

            val line1 = Line(
                start = line.start,
                end = gapStart,
                strokeWidth = line.strokeWidth,
                arrowHeadSize = line.arrowHeadSize,
            )
            val line2 = Line(
                start = gapEnd,
                end = line.end,
                strokeWidth = line.strokeWidth,
                arrowHeadSize = line.arrowHeadSize,
            )

            drawLine(
                color = line.color,
                start = line1.start,
                end = line1.end,
                strokeWidth = line1.strokeWidth.toPx(),
                cap = StrokeCap.Butt
            )

            drawLine(
                color = line.color,
                start = line2.start,
                end = line2.end,
                strokeWidth = line2.strokeWidth.toPx(),
                cap = StrokeCap.Butt
            )

            val rotationAngle = if (isVerticalLine) -90f else 0f
            val textX = centerX - textLayoutResult.size.width / 2
            val textY = centerY + textLayoutResult.size.height / 2 + textSpacing.toPx()
            rotate(rotationAngle, pivot = Offset(centerX, centerY)) {
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(textX, textY),
                )
            }
        }


        currentLine?.let { line ->
            val centerX = (line.start.x + line.end.x) / 2
            val centerY = (line.start.y + line.end.y) / 2

            val textLayoutResult: TextLayoutResult =
                textMeasurer.measure(
                    text = line.text,
                    style = TextStyle(color = line.color, fontSize = 15.sp),
                )
            val gapSize = textLayoutResult.size.width.dp
            val isVerticalLine = abs(line.end.y - line.start.y) > abs(line.end.x - line.start.x)
            val angle = atan2(line.end.y - line.start.y, line.end.x - line.start.x)

            val gapStart = Offset(
                centerX - gapSize.toPx() / 2 * cos(angle),
                centerY - gapSize.toPx() / 2 * sin(angle)
            )
            val gapEnd = Offset(
                centerX + gapSize.toPx() / 2 * cos(angle),
                centerY + gapSize.toPx() / 2 * sin(angle)
            )

            val line1 = Line(
                start = line.start,
                end = gapStart,
                strokeWidth = line.strokeWidth,
                arrowHeadSize = line.arrowHeadSize,
            )
            val line2 = Line(
                start = gapEnd,
                end = line.end,
                strokeWidth = line.strokeWidth,
                arrowHeadSize = line.arrowHeadSize,
            )

            drawLine(
                color = line.color,
                start = line1.start,
                end = line1.end,
                strokeWidth = line1.strokeWidth.toPx(),
                cap = StrokeCap.Butt
            )

            drawLine(
                color = line.color,
                start = line2.start,
                end = line2.end,
                strokeWidth = line2.strokeWidth.toPx(),
                cap = StrokeCap.Butt
            )

            drawArrowheads(
                start = line.start,
                end = line.end,
                color = line.color,
                size = line.arrowHeadSize
            )

            val textX = centerX - textLayoutResult.size.width / 2
            val textY = centerY + textLayoutResult.size.height / 2 + textSpacing.toPx()
            val rotationAngle = if (isVerticalLine) -90f else 0f
            rotate(rotationAngle, pivot = Offset(centerX, centerY)) {
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(textX, textY),
                )
            }
        }
    }
}


private fun DrawScope.drawArrowheads(start: Offset, end: Offset, color: Color, size: Dp) {
    val arrowLength = size.toPx()
    val arrowAngle = 40f

    val angle = atan2(end.y - start.y, end.x - start.x)

    val arrowPoint1Start = Offset(
        (start.x + arrowLength * cos(angle - Math.toRadians(arrowAngle.toDouble()))).toFloat(),
        (start.y + arrowLength * sin(angle - Math.toRadians(arrowAngle.toDouble()))).toFloat()
    )
    val arrowPoint2Start = Offset(
        (start.x + arrowLength * cos(angle + Math.toRadians(arrowAngle.toDouble()))).toFloat(),
        (start.y + arrowLength * sin(angle + Math.toRadians(arrowAngle.toDouble()))).toFloat()
    )

    val arrowPoint1End = Offset(
        (end.x - arrowLength * cos(angle - Math.toRadians(arrowAngle.toDouble()))).toFloat(),
        (end.y - arrowLength * sin(angle - Math.toRadians(arrowAngle.toDouble()))).toFloat()
    )
    val arrowPoint2End = Offset(
        (end.x - arrowLength * cos(angle + Math.toRadians(arrowAngle.toDouble()))).toFloat(),
        (end.y - arrowLength * sin(angle + Math.toRadians(arrowAngle.toDouble()))).toFloat()
    )

    drawLine(color, start, arrowPoint1Start, size.toPx(), cap = StrokeCap.Round)
    drawLine(color, start, arrowPoint2Start, size.toPx(), cap = StrokeCap.Round)
    drawLine(color, end, arrowPoint1End, size.toPx(), cap = StrokeCap.Round)
    drawLine(color, end, arrowPoint2End, size.toPx(), cap = StrokeCap.Round)
}


data class Line(
    val id: Int = 0,
    val start: Offset = Offset.Zero,
    var end: Offset = Offset.Zero,
    val strokeWidth: Dp = 5.dp,
    val arrowHeadSize: Dp = 7.dp,
    var text: String = "",
    var color: Color = Color.Blue,
    var isDraggingStartPoint: Boolean = false,
    var isDraggingEndPoint: Boolean = false,
    var isDraggingWholeLine: Boolean = false
)


