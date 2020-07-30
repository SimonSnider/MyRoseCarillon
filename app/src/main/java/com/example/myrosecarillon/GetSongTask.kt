package com.example.myrosecarillon

import android.os.AsyncTask
import android.util.Log
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference

class GetSongTask(private var songConsumer: SongConsumer): AsyncTask<DocumentReference, Void, Song>() {
    override fun doInBackground(vararg params: DocumentReference?): Song? {
        val songRef = params[0]
        return try {
            val song = songRef?.get()?.result?.let { Song.fromSnapshot(it) }
            song
        } catch (e: Exception) {
            Log.e(Constants.TAG, "ASYNC TASK EXCEPTION: " + e.toString())
            null
        }
    }

    override fun onPostExecute(result: Song?) {
        super.onPostExecute(result)
        songConsumer.onSongLoaded(result)
    }

    interface SongConsumer {
        fun onSongLoaded(song: Song?)
    }
}