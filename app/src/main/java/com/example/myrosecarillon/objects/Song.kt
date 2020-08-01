package com.example.myrosecarillon.objects

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.util.*

class Song (var creatorID: String = "", var midi: String = "", var title: String = "", var creationDate: Timestamp) {
    @get:Exclude var id = ""
    @get:Exclude var user: User? = null

    companion object {
        const val DATE_KEY = "creationDate"
        const val CREATOR_ID_KEY = "creatorID"

        fun fromSnapshot(snapshot: DocumentSnapshot): Song {
            val song: Song = snapshot.toObject(
                Song::class.java)!!
            song.id = snapshot.id
//            if (song.userRef != null) {
//                song.user =
//                    User.fromSnapshot(song.userRef!!.get().result!!)
//            }
            return song
        }
    }
}