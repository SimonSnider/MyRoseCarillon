package com.example.myrosecarillon.midiEditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.myrosecarillon.GetMidiTask
import com.example.myrosecarillon.R
import com.leff.midi.event.MidiEvent
import com.leff.midi.util.MidiEventListener
import java.io.File
import kotlin.math.abs

class MidiComposerView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var lines: Int = 6
    private var bars: Int = 2
    private var lineColor: Int
    private var showLines: Boolean
    private var showBars: Boolean
    private var isDisplay: Boolean = false
    private val linePaint: Paint
    private val barPaint: Paint
    private val notePaint: Paint
    private var midiStructure: MidiStructure? = null
    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            play()
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent?) {
            midiStructure?.placeAtPointer()
            midiStructure?.movePointerRight()
            postInvalidate()
            super.onLongPress(e)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            midiStructure = MidiStructure(lines, bars)
            postInvalidate()
            return super.onDoubleTap(e)
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
                isDisplay = getBoolean(R.styleable.MidiComposerView_isDisplay, IS_DISPLAY_DEFAULT)
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
        if(isDisplay)
            return false
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

    @SuppressLint("UseCompatLoadingForDrawables")
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
                    var noteX = ((note.column + 1) * width / ((bars * 8) + 1))
                    var noteY = ((note.row + 1) * height / (lines + 1))
                    var noteRad = height / (lines + 1) / 2
                    var d: Drawable = when(note.type) {
                        MidiStructure.WHOLE_NOTE -> resources.getDrawable(R.drawable.ic_whole_note, null)
                        MidiStructure.HALF_NOTE -> resources.getDrawable(R.drawable.ic_half_note, null)
                        MidiStructure.QUARTER_NOTE -> resources.getDrawable(R.drawable.ic_quarter_note, null)
                        MidiStructure.EIGHTH_NOTE -> resources.getDrawable(R.drawable.ic_eighth_note, null)
                        MidiStructure.EIGHTH_REST -> resources.getDrawable(R.drawable.ic_eighth_rest, null)
                        else -> resources.getDrawable(R.drawable.ic_eighth_note, null)
                    }

                    Log.d(DEBUG_TAG, "note found $noteX, $noteY")

                    d.setBounds(noteX - noteRad, noteY - (noteRad * 18 / 12), noteX + noteRad, noteY + (noteRad * 6 / 12))

                    d.draw(this)
                }
            }

            //Draw the selected Note
            run{
                if(!isDisplay) {
                    drawCircle(
                        (((midiStructure?.getPointerX()
                            ?: 0) + 1) * width / ((bars * 8) + 1).toFloat()),
                        (((midiStructure?.getPointerY()
                            ?: 0) + 1) * height / (lines + 1)).toFloat(),
                        height / 40F,
                        notePaint
                    )
                }
            }

        }
    }

    fun setPointerNote(noteNum: Int){
        midiStructure?.setPointerType(noteNum)
    }

    fun getMidi(): File? {
        return midiStructure?.toMidi(context)
    }

    fun resetMidi(){
        midiStructure = MidiStructure(lines, bars)
        postInvalidate()
    }

    fun sendMidi(url: String) {
        GetMidiTask(this).execute(url)
    }

    fun updateMidi(midiFile: File){
        midiStructure?.loadMidi(midiFile)
        postInvalidate()
    }

    fun play() {
        var mediaPlayer: MediaPlayer? = MediaPlayer.create(context, midiStructure!!.toMidi(context).toUri())
        mediaPlayer?.start()
    }

    fun getMidiStructure(): MidiStructure? = midiStructure

    companion object Constants{
        const val BAR_DEFAULT = 2
        const val LINE_DEFAULT = 6
        const val LINE_COLOR_DEFAULT = android.R.color.darker_gray
        const val SHOW_LINES_DEFAULT = true
        const val SHOW_BARS_DEFAULT = false
        const val IS_DISPLAY_DEFAULT = false
        const val DEBUG_TAG = "MIDICompView"
    }
}