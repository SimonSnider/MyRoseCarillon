package com.example.myrosecarillon.midiEditor

import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.leff.midi.MidiFile
import com.leff.midi.MidiTrack
import com.leff.midi.event.MidiEvent
import com.leff.midi.event.NoteOn
import com.leff.midi.event.meta.Tempo
import com.leff.midi.event.meta.TimeSignature
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


class MidiStructure (var rows: Int, var bars: Int) {

    private var noteGrid = Array(rows) { IntArray(8 * bars)}
    private var pointerNote = Note(EIGHTH_NOTE, 0, 0)
    private var pitches: List<Int>? = null

    init {
        for( i in 0 until (bars * 8)){
            for(j in 0 until (rows)){
                Log.d(MidiComposerView.DEBUG_TAG, "${i},${j}")
                noteGrid[j][i] =
                    EMPTY
            }
        }
    }

    //TODO Finish Implementation
    fun checkLocation(row: Int, column: Int) : Boolean{
        return true
    }

    //TODO Finish Implementation
    fun remove(row: Int, column: Int) : Boolean{
        return true
    }

    fun getNotes() : List<Note>{
        var notes = ArrayList<Note>()
        for( i in 0 until (bars * 8)){
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
        try {
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
        }catch (e: Exception){
            return false
        }
    }

    fun addQuarterNote(row: Int, column: Int) : Boolean{
        try {
            return if (noteGrid[row][column] == EMPTY && noteGrid[row][column + 1] == EMPTY) {
                noteGrid[row][column] =
                    QUARTER_NOTE
                noteGrid[row][column + 1] =
                    QUARTER_NOTE_FILLER
                true
            } else {
                false
            }
        }catch (e: Exception){
            return false
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

    fun setPointerType(noteType: Int){
        pointerNote.type = noteType
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
        if(pointerNote.column < (bars * 8) - getNoteScale(pointerNote.type)){
            pointerNote.column += getNoteScale(pointerNote.type)
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

    fun setPitchArgs(args: List<Int>) {
        pitches = args
    }

    private fun getNoteScale(noteType: Int) = when(noteType){
        WHOLE_NOTE -> 8
        HALF_NOTE -> 4
        QUARTER_NOTE -> 2
        EIGHTH_NOTE -> 1
        else -> 1
    }

    fun toMidi(context: Context): File{

        Log.d(DEBUG_TAG, "toMidi Called")

        var tempoTrack = MidiTrack()
        var noteTrack = MidiTrack()

        var ts = TimeSignature()
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION)

        var tempo = Tempo()
        tempo.bpm = 228F

        tempoTrack.insertEvent(ts)
        tempoTrack.insertEvent(tempo)

        for( i in 0 until (bars * 8)){
            for(j in 0 until (rows)){
                if(noteGrid[j][i] % 2 == 0){
                    val channel = 0
                    val pitch = DEFAULT_PENTATONIC_PITCHES[j]
                    val velocity = 80
                    val tick = i * 480.toLong()
                    val duration: Long = (120 * getNoteScale(noteGrid[j][i])).toLong()
                    noteTrack.insertNote(channel, pitch, velocity, tick, duration)
                }
            }
        }

        val tracks: MutableList<MidiTrack> = ArrayList()
        tracks.add(tempoTrack)
        tracks.add(noteTrack)

        val midi = MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks)

        val output = File.createTempFile("example", ".mid", context.codeCacheDir)
        output.deleteOnExit()
        try {
            midi.writeToFile(output)
        } catch (e: IOException) {
            Log.d(DEBUG_TAG, e.toString())
        }

        Log.d(DEBUG_TAG, "Midi Created")

        return output

    }

    fun loadMidi(file: File){
        Log.d(DEBUG_TAG, "Midi Received")

        val midi = MidiFile(file)
        val track = midi.tracks[1]
        val trackIterator = track.events.iterator()


        for(event in trackIterator){
            var notePos = (event.tick / 480).toInt()
            if(notePos == 1) notePos--
            var noteDelta = (event.delta / 120).toInt()
            var notePitch = (event as NoteOn).noteValue
            Log.d(DEBUG_TAG, "Midi Event Found at ${(event as NoteOn).velocity}")
            if(notePos < noteGrid[0].size && DEFAULT_PENTATONIC_PITCHES.indexOf(notePitch) < noteGrid.size && (event as NoteOn).velocity == 0)
                when(noteDelta){
                    8 -> addWholeNote(DEFAULT_PENTATONIC_PITCHES.indexOf(notePitch), notePos)
                    4 -> addHalfNote(DEFAULT_PENTATONIC_PITCHES.indexOf(notePitch), notePos)
                    2 -> addQuarterNote(DEFAULT_PENTATONIC_PITCHES.indexOf(notePitch), notePos)
                    1 -> addEighthNote(DEFAULT_PENTATONIC_PITCHES.indexOf(notePitch), notePos)
                }

        }

    }

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

        const val DEBUG_TAG = "MidiStruct"

        val DEFAULT_MAJOR_PITCHES = ArrayList<Int>().apply {
            add(40)
            add(42)
            add(44)
            add(45)
            add(47)
            add(49)
            add(51)
            add(52)}

        val DEFAULT_PENTATONIC_PITCHES = ArrayList<Int>().apply {
            add(52)
            add(49)
            add(47)
            add(44)
            add(42)
            add(40) }
    }

}