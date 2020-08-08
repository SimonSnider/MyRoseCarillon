package com.example.myrosecarillon.midiEditor

import android.util.Log
import kotlin.math.abs

class MidiStructure (var rows: Int, var bars: Int) {

    private var noteGrid = Array(rows) { IntArray(8 * bars)}
    private var pointerNote = Note(EIGHTH_NOTE, 0, 0)

    init {
        for( i in 0 until (bars * 8 - 1)){
            for(j in 0 until (rows)){
                Log.d(MidiComposerView.DEBUG_TAG, "${i},${j}")
                noteGrid[j][i] =
                    EMPTY
            }
        }
    }

    fun checkLocation(row: Int, column: Int) : Boolean{
        return true
    }

    fun remove(row: Int, column: Int) : Boolean{
        return true
    }

    fun getNotes() : List<Note>{
        var notes = ArrayList<Note>()
        for( i in 0 until (bars * 8 - 1)){
            for(j in 0 until (rows)){
                if(noteGrid[j][i] % 2 == 0){
                    notes.add(
                        Note(
                            noteGrid[j][i],
                            j,
                            i
                        )
                    )
                }
            }
        }
        return notes
    }

    fun addWholeNote(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY && noteGrid[row][column + 1] == EMPTY && noteGrid[row][column + 2] == EMPTY && noteGrid[row][column + 3] == EMPTY && noteGrid[row][column + 4] == EMPTY && noteGrid[row][column + 5] == EMPTY && noteGrid[row][column + 6] == EMPTY && noteGrid[row][column + 7] == EMPTY){
            noteGrid[row][column] =
                WHOLE_NOTE
            noteGrid[row][column+1] =
                WHOLE_NOTE_FILLER
            noteGrid[row][column+2] =
                WHOLE_NOTE_FILLER
            noteGrid[row][column+3] =
                WHOLE_NOTE_FILLER
            noteGrid[row][column+4] =
                WHOLE_NOTE_FILLER
            noteGrid[row][column+5] =
                WHOLE_NOTE_FILLER
            noteGrid[row][column+6] =
                WHOLE_NOTE_FILLER
            noteGrid[row][column+7] =
                WHOLE_NOTE_FILLER
            true
        }else {
            false
        }
    }

    fun addHalfNote(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY && noteGrid[row][column + 1] == EMPTY && noteGrid[row][column + 2] == EMPTY && noteGrid[row][column + 3] == EMPTY){
            noteGrid[row][column] =
                HALF_NOTE
            noteGrid[row][column+1] =
                HALF_NOTE_FILLER
            noteGrid[row][column+2] =
                HALF_NOTE_FILLER
            noteGrid[row][column+3] =
                HALF_NOTE_FILLER
            true
        }else {
            false
        }
    }

    fun addQuarterNote(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY && noteGrid[row][column + 1] == EMPTY){
            noteGrid[row][column] =
                QUARTER_NOTE
            noteGrid[row][column+1] =
                QUARTER_NOTE_FILLER
            true
        }else {
            false
        }
    }

    fun addEighthNote(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY){
            noteGrid[row][column] =
                EIGHTH_NOTE
            true
        }else {
            false
        }
    }

    fun addWholeRest(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY && noteGrid[row][column + 1] == EMPTY && noteGrid[row][column + 2] == EMPTY && noteGrid[row][column + 3] == EMPTY && noteGrid[row][column + 4] == EMPTY && noteGrid[row][column + 5] == EMPTY && noteGrid[row][column + 6] == EMPTY && noteGrid[row][column + 7] == EMPTY){
            noteGrid[row][column] =
                WHOLE_REST
            noteGrid[row][column+1] =
                WHOLE_REST_FILLER
            noteGrid[row][column+2] =
                WHOLE_REST_FILLER
            noteGrid[row][column+3] =
                WHOLE_REST_FILLER
            noteGrid[row][column+4] =
                WHOLE_REST_FILLER
            noteGrid[row][column+5] =
                WHOLE_REST_FILLER
            noteGrid[row][column+6] =
                WHOLE_REST_FILLER
            noteGrid[row][column+7] =
                WHOLE_REST_FILLER
            true
        }else {
            false
        }
    }

    fun addHalfRest(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY && noteGrid[row][column + 1] == EMPTY && noteGrid[row][column + 2] == EMPTY && noteGrid[row][column + 3] == EMPTY){
            noteGrid[row][column] =
                HALF_REST
            noteGrid[row][column+1] =
                HALF_REST_FILLER
            noteGrid[row][column+2] =
                HALF_REST_FILLER
            noteGrid[row][column+3] =
                HALF_REST_FILLER
            true
        }else {
            false
        }
    }

    fun addQuarterRest(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY && noteGrid[row][column + 1] == EMPTY){
            noteGrid[row][column] =
                QUARTER_REST
            noteGrid[row][column+1] =
                QUARTER_REST_FILLER
            true
        }else {
            false
        }
    }

    fun addEighthRest(row: Int, column: Int) : Boolean{
        return if(noteGrid[row][column] == EMPTY){
            noteGrid[row][column] =
                EIGHTH_REST
            true
        }else {
            false
        }
    }

    fun placeAtPointer(): Boolean{
        return when(pointerNote.type){
            WHOLE_NOTE -> addWholeNote(pointerNote.row, pointerNote.column)
            HALF_NOTE -> addHalfNote(pointerNote.row, pointerNote.column)
            QUARTER_NOTE -> addQuarterNote(pointerNote.row, pointerNote.column)
            EIGHTH_NOTE -> addEighthNote(pointerNote.row, pointerNote.column)
            WHOLE_REST -> addWholeRest(pointerNote.row, pointerNote.column)
            HALF_REST -> addHalfRest(pointerNote.row, pointerNote.column)
            QUARTER_REST -> addQuarterRest(pointerNote.row, pointerNote.column)
            EIGHTH_REST -> addEighthRest(pointerNote.row, pointerNote.column)
            else -> false
        }
    }

    fun movePointerUp(): Boolean{
        pointerNote.row = (pointerNote.row - 1)
        if (pointerNote.row < 0) pointerNote.row = rows - 1
        return true
    }

    fun movePointerDown(): Boolean{
        pointerNote.row = (pointerNote.row + 1) % rows
        return true
    }

    fun movePointerRight(): Boolean{
        if(pointerNote.column < (bars * 8) - 1){
            pointerNote.column += 1
            return true
        }
        return false
    }

    fun movePointerLeft(): Boolean{
        if(pointerNote.column > 0){
            pointerNote.column -= 1
            return true
        }
        return false
    }

    fun getPointerX() = pointerNote.column

    fun getPointerY() = pointerNote.row

    //fun toMidi()

    companion object {
        const val EMPTY = -1
        const val WHOLE_NOTE = 0
        const val WHOLE_NOTE_FILLER = 1
        const val HALF_NOTE = 2
        const val HALF_NOTE_FILLER = 3
        const val QUARTER_NOTE = 4
        const val QUARTER_NOTE_FILLER = 5
        const val EIGHTH_NOTE = 6
        const val WHOLE_REST = 8
        const val WHOLE_REST_FILLER = 9
        const val HALF_REST = 10
        const val HALF_REST_FILLER = 11
        const val QUARTER_REST = 12
        const val QUARTER_REST_FILLER = 13
        const val EIGHTH_REST = 14
    }

}