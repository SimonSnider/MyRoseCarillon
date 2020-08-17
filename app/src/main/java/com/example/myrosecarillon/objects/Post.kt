package com.example.myrosecarillon.objects

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.util.*

class Post(
    var date: Timestamp? = null, var likes: Int = 0, var dislikes: Int = 0,
    var rating: Int = 0, var userRef: DocumentReference? = null,
    var songRef: DocumentReference? = null,
    var votes: MutableMap<String, Int>? = mutableMapOf()) {
    @get:Exclude var id = ""
    @get:Exclude var user: User? = null
    @get:Exclude var song: Song? = null

    override fun toString(): String {
        return "$id, $rating"
    }

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