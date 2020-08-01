package com.example.myrosecarillon

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.firestore.*

class MySongsAdapter(val context: Context):  RecyclerView.Adapter<SongViewHolder>(){

    private val songs = ArrayList<Song>()
    private val songsRef = FirebaseFirestore.getInstance().collection(Constants.SONGS_PATH)
    private lateinit var listenerRegistration: ListenerRegistration
    private val currentUserID = "MRXkKWXrxMDwkDL77qoi"

    fun addSnapshotListener() {
        listenerRegistration = songsRef
            .whereEqualTo(Song.CREATOR_ID_KEY, currentUserID)
            .orderBy(Song.DATE_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener{querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        for (documentChange in querySnapshot.documentChanges) {
            val song = Song.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $song")
                    songs.add(0, song)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $song")
                    val index = songs.indexOfFirst{it.id == song.id}
                    songs.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $song")
                    val index = songs.indexOfFirst { it.id == song.id }
                    songs[index] = song
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.vote_song_card, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }
}