package com.example.smarthome_monitoring_system.view.floors

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GridOverlayView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defaultStyleAttribute: Int = 0
) : View(
    context,
    attributes,
    defaultStyleAttribute
) {

    private val gridPaint = Paint().apply {
        color = Color.parseColor("#1A1A237E") // 10% Indigo
        strokeWidth = 1f * resources.displayMetrics.density
        style = Paint.Style.STROKE
    }

    private var rowCount = 8
    private var columnCount = 8

    fun setGridSize(rows: Int, columns: Int) {
        if (rows <= 0 || columns <= 0) {
            return
        }

        rowCount = rows
        columnCount = columns

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cellWidth = width.toFloat() / columnCount
        val cellHeight = height.toFloat() / rowCount

        for (column in 0..columnCount) {
            val xPosition = column * cellWidth

            canvas.drawLine(
                xPosition,
                0f,
                xPosition,
                height.toFloat(),
                gridPaint
            )
        }

        for (row in 0..rowCount) {
            val yPosition = row * cellHeight

            canvas.drawLine(
                0f,
                yPosition,
                width.toFloat(),
                yPosition,
                gridPaint
            )
        }
    }
}