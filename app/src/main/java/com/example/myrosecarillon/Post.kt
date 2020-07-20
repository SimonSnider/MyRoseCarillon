package com.example.myrosecarillon

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.util.*

class Post(date: Date? = null, likes: Int = 0, dislikes: Int = 0, user: User? = null, song: Song? = null) {
    @get:Exclude var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Post {
            val post = snapshot.toObject(Post::class.java)!!
            post.id = snapshot.id
            return post
        }
    }
}