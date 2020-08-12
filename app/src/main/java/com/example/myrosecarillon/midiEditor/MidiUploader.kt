package com.example.myrosecarillon.midiEditor

import android.net.Uri
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Song
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File

class MidiUploader{

    private val storageRef = FirebaseStorage.getInstance().reference.child(FOLDER)
    private val thumbnailRef = FirebaseFirestore.getInstance().collection(Constants.SONGS_PATH)
    private val auth = FirebaseAuth.getInstance()

    fun storageAdd(file: File, title: String){
        val baos = ByteArrayOutputStream()
        val data = file.readBytes()
        val id = file.name
        var uploadTask = storageRef.child(id).putBytes(data)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation storageRef.child(id).downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val downloadUri = task.result.toString()
                auth.currentUser?.uid?.let { Song(it, downloadUri, title, Timestamp.now()) }?.let {
                    thumbnailRef.add(
                        it
                    )
                }
            }
        }
    }

    companion object{
        const val FOLDER = "midis"
        const val DEBUG_TAG = "MidiUploader"
    }

}