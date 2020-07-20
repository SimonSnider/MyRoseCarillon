package com.example.myrosecarillon.objects

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class Song (var userRef: DocumentReference? = null, var midi: String = "", var title: String = "") {
    @get:Exclude var id = ""
    @get:Exclude var user: User? = null

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Song {
            val song: Song = snapshot.toObject(
                Song::class.java)!!
            song.id = snapshot.id
            if (song.userRef != null) {
                song.user =
                    User.fromSnapshot(song.userRef!!.get().result!!)
            }
            return song
        }
    }
}