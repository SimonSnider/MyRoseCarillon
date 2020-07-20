package com.example.myrosecarillon

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class Song (midi: String = "", title: String = "", score: Score? = null, creator: User? = null) {
    @get:Exclude var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Song {
            val song = snapshot.toObject(Song::class.java)!!
            song.id = snapshot.id
            return song
        }
    }
}