package com.example.myrosecarillon

import android.os.AsyncTask
import android.os.FileUtils.copy
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.midiEditor.MidiComposerView
import java.io.File
import java.io.FileOutputStream

class GetMidiTask(var midiCompView: MidiComposerView) : AsyncTask<String, Void, File>() {
    override fun doInBackground(vararg urls: String?): File {
        val inStream = java.net.URL(urls[0]).openStream()

        val temp = File.createTempFile("downloaded", Constants.MIDI_SUFFIX)
        temp.deleteOnExit()

        val outStream = FileOutputStream(temp)

        copy(inStream, outStream)

        return temp
    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)
        if (result != null) {
            midiCompView.updateMidi(result)
        }
    }
}