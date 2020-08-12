package com.example.myrosecarillon

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage

class MySongsAdapter(val context: Context):  RecyclerView.Adapter<SongViewHolder>(){

    private val songs = ArrayList<Song>()
    private val songsRef = FirebaseFirestore.getInstance().collection(Constants.SONGS_PATH)
    private val storageRef = FirebaseStorage.getInstance().reference.child("midis")
    private lateinit var listenerRegistration: ListenerRegistration
    private val auth = FirebaseAuth.getInstance()

    fun addSnapshotListener() {
        listenerRegistration = songsRef
            .whereEqualTo(Song.CREATOR_ID_KEY, auth.currentUser?.uid)
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
        val view = LayoutInflater.from(context).inflate(R.layout.my_song_card, parent, false)
        return SongViewHolder(view, this)
    }

    override fun getItemCount() = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    fun showDeleteDialog(position: Int){
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle("Are you sure you want to delete the song ${songs[position].title}?")
        builder.setPositiveButton(android.R.string.ok) {_, _ ->
            Log.d(Constants.TAG, "Deleting song ${songs[position].id}")
            val url = songs[position].midi
            var name = url.substring(url.indexOf("%2F") + 3, (url.indexOf('?')))
            name = name.replace("%20"," ");
            storageRef.child(name).delete()
            songsRef.document(songs[position].id).delete()
        }
        builder.setNegativeButton(android.R.string.cancel) {_,_ ->

        }
        builder.create().show()
    }
}