package com.example.myrosecarillon.midiEditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.myrosecarillon.R

class MidiComposerView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var lines: Int
    private var bars: Int
    private var lineColor: Int
    private var showLines: Boolean
    private var showBars: Boolean
    private val linePaint: Paint
    private val barPaint: Paint
    private val notePaint: Paint
    private var midiStructure: MidiStructure? = null
    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            midiStructure?.placeAtPointer()
            midiStructure?.movePointerRight()
            postInvalidate()
            super.onLongPress(e)
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if(velocityY > 0){
                midiStructure?.movePointerUp()
            }else if(velocityY < 0){
                midiStructure?.movePointerDown()
            }
            postInvalidate()
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
    private val detector: GestureDetector = GestureDetector(context, myListener)

    init {
        context.theme.obtainStyledAttributes(attributeSet, R.styleable.MidiComposerView, 0, 0).apply {
            try{
                lines = getInteger(R.styleable.MidiComposerView_lines, LINE_DEFAULT)
                bars = getInteger(R.styleable.MidiComposerView_bars, BAR_DEFAULT)
                lineColor = getColor(R.styleable.MidiComposerView_lineColor, ContextCompat.getColor(context, LINE_COLOR_DEFAULT))
                showLines = getBoolean(R.styleable.MidiComposerView_showLines, SHOW_LINES_DEFAULT)
                showBars = getBoolean(R.styleable.MidiComposerView_showBars, SHOW_BARS_DEFAULT)
            } finally {
                recycle()
            }
        }
        linePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = lineColor
            style = Paint.Style.STROKE
            strokeWidth = 1F
        }
        barPaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorDetailDark)
            style = Paint.Style.STROKE
            strokeWidth = 1F
        }
        notePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, android.R.color.black)
            style = Paint.Style.FILL
        }
        midiStructure = MidiStructure(
            lines,
            bars
        )

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return detector.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var xpad = (paddingLeft + paddingRight).toFloat()
        var ypad = (paddingTop + paddingBottom).toFloat()

        Log.d(DEBUG_TAG, (h).toString())
        Log.d(DEBUG_TAG, (w).toString())

        linePaint.strokeWidth = ((h - ypad) * .01).toFloat()
        barPaint.strokeWidth = ((w - xpad) * .005).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var xpad = (paddingLeft + paddingRight).toFloat()
        var ypad = (paddingTop + paddingBottom).toFloat()

        canvas.apply {

            //Draw the lines for the view
            run {
                if (showBars && bars > 1) {
                    for (i in 1 until bars) {
                        drawLine(
                            0F + (i * width / (bars)),
                            0F + paddingTop,
                            0F + +(i * width / (bars)),
                            0F + height - paddingBottom,
                            barPaint
                        )
                    }
                }

                if (showLines) {
                    for (i in 1..lines) {
                        drawLine(
                            0F + paddingLeft,
                            0F + (i * height / (lines + 1)),
                            0F + width - paddingRight,
                            0F + (i * height / (lines + 1)),
                            linePaint
                        )
                    }
                }
            }

            //Draw the notes for the view
            run{
                midiStructure?.getNotes()?.forEach { note ->
                    Log.d(DEBUG_TAG, "note found ${(note.column * width / (bars)).toFloat()}, ${(note.row * height / (lines + 1)).toFloat()}")
                    drawCircle(((note.column + 1) * width / (bars * 8)).toFloat(), ((note.row + 1) * height / (lines + 1)).toFloat(), 50F, notePaint)
                }
            }

            run{
                drawCircle((((midiStructure?.getPointerX() ?: 0) + 1) * width / (bars * 8)).toFloat(), (((midiStructure?.getPointerY()
                    ?: 0) + 1) * height / (lines + 1)).toFloat(), 100F, notePaint)
            }

        }
    }

    companion object Constants{
        const val BAR_DEFAULT = 1
        const val LINE_DEFAULT = 5
        const val LINE_COLOR_DEFAULT = android.R.color.darker_gray
        const val SHOW_LINES_DEFAULT = true
        const val SHOW_BARS_DEFAULT = false
        const val DEBUG_TAG = "MIDICompView"
    }
}