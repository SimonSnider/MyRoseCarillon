package com.example.myrosecarillon.objects

import android.util.Log
import com.example.myrosecarillon.constants.Constants
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.util.*

class Post(var date: Date? = null, var likes: Int = 0, var dislikes: Int = 0,
           var rating: Int = 0, var userRef: DocumentReference? = null,
           var songRef: DocumentReference? = null,
           var votes: MutableMap<String, Int>? = null) {
    @get:Exclude var id = ""
    @get:Exclude var user: User? = null
    @get:Exclude var song: Song? = null

    companion object {
        const val RATING_KEY = "rating"

        fun fromSnapshot(snapshot: DocumentSnapshot): Post {
            val post = snapshot.toObject(Post::class.java)!!
            post.id = snapshot.id
//            if (post.userRef != null){
//                post.user =
//                    User.fromSnapshot(post.userRef!!.get().result!!)
//            }
//            if (post.songRef != null){
//                post.song =
//                    Song.fromSnapshot(post.songRef!!.get().result!!)
//            }
            return post
        }
    }
}