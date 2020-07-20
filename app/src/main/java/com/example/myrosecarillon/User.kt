package com.example.myrosecarillon

import android.graphics.Bitmap
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class User(picture: Bitmap? = null, displayName: String = "", username: String = "", carills: Int = 0, darkMode: Boolean = false) {
    @get:Exclude var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): User {
            val user = snapshot.toObject(User::class.java)!!
            user.id = snapshot.id
            return user
        }
    }
}