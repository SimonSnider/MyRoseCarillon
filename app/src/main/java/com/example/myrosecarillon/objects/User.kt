package com.example.myrosecarillon.objects

import android.graphics.Bitmap
import com.example.myrosecarillon.constants.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class User(var pictureUrl: String = Constants.DEFAULT_PICTURE_PATH, var displayName: String = "", var carills: Int = 0, var darkMode: Boolean = false, var upvotesReceived: Int = 0, var songsUploaded: Int = 0) {
    @get:Exclude var id = ""


    fun getUpvote(previous: Int?, new: Int) {
        if (previous == 1 && new <= previous){
            upvotesReceived--
        } else if (new == 1) upvotesReceived++

        Constants.userRef.document(id).set(this)

    }

    fun uploadSong() {
        this.songsUploaded++
        Constants.userRef.document(id).set(this)
    }

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): User {
            val user = snapshot.toObject(User::class.java)!!
            user.id = snapshot.id
            return user
        }
    }
}