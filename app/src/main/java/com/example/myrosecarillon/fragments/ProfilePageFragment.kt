package com.example.myrosecarillon.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myrosecarillon.R
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Song
import com.example.myrosecarillon.objects.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_page.view.*
import kotlinx.android.synthetic.main.settings_dialog.view.*
import java.io.ByteArrayOutputStream
import java.lang.Math.abs
import java.net.URI
import kotlin.random.Random


const val GET_IMAGE_REQUEST_CODE = 100
class ProfilePageFragment : Fragment() {
    private val userRef = FirebaseFirestore.getInstance().collection(Constants.USERS_PATH)
    private val auth = FirebaseAuth.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference.child("Pictures")
    private val songsRef = FirebaseFirestore.getInstance().collection(Constants.SONGS_PATH)
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_page, container, false)
        //gets the data from the user object and displays it on the fragment
        auth.currentUser?.uid?.let {
            userRef.document(it).get().addOnSuccessListener { snap ->
                user = User.fromSnapshot(snap)
                view.name_text_view.text = user.displayName
                view.carills_stat_text_view.text = String.format(requireContext().resources.getString(R.string.carills_var), user.carills)
                view.songs_played_stat_text_view.text = String.format(requireContext().resources.getString(R.string.songs_played_var), 0)
                view.songs_uploaded_stat_text_view.text = String.format(requireContext().resources.getString(R.string.songs_uploaded_var), user.songsUploaded)
                view.upvotes_given_stat_text_view.text = String.format(requireContext().resources.getString(R.string.upvotes_given_var), user.upvotesReceived)
                Picasso.get().load(user.pictureUrl).into(view.findViewById<ImageView>(R.id.profile_imageView))
            }
        }
        //initializes the navigation and settings button
        view.my_songs_button.setOnClickListener{
            findNavController().navigate(R.id.action_profilePageFragment_to_mySongsFragment)
        }
        view.user_settings_button.setOnClickListener{
            launchSettingsDialog()
        }
        return view
    }

    //launches the settings dialog to allow the user to change their profile picture, display name, and light/dark preferences
    private fun launchSettingsDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("User Settings")

        val view = layoutInflater.inflate(R.layout.settings_dialog, null, false)
        builder.setView(view)
        if (user.darkMode) view.light_dark_toggle_button.toggle()
        view.display_name_edit_text.setText(user.displayName)

        //launches an intent to choose a picture from the device
        view.choose_profile_picture_button.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GET_IMAGE_REQUEST_CODE)
        }

        //confirms the changes made and updates the user document on the database
        builder.setPositiveButton(android.R.string.ok){_,_ ->
            if (!view.display_name_edit_text.text.isBlank()) {
                user.displayName = view.display_name_edit_text.text.toString()
                requireView().name_text_view.text  = user.displayName
            } else Toast.makeText(context, "Display Name cannot be empty", Toast.LENGTH_SHORT).show()

            user.darkMode = view.light_dark_toggle_button.isChecked
            //TODO: change color pallete

            userRef.document(user.id).set(user)
        }
        builder.setNegativeButton(android.R.string.cancel){_,_ ->}

        builder.create().show()
    }

    //catches the result of the get image intent and updates the user's profile picture
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == GET_IMAGE_REQUEST_CODE) {
            val image: Uri? = data?.data
            if (image != null) {

                //sends the new picture to the database storage
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, image)
                view?.profile_imageView?.setImageBitmap(bitmap)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val outData = baos.toByteArray()
                val id = abs(Random.nextLong()).toString()
                val uploadTask = storageRef.child(id).putBytes(outData)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task ->
                    task.exception?.let {throw it}

                    return@Continuation storageRef.child(id).downloadUrl
                }).addOnCompleteListener { task ->
                    //saves the old picture url for deletion and sets the user's picture url to the newly stored one
                    val oldPictureUrl = user.pictureUrl
                    user.pictureUrl = task.result.toString()
                    userRef.document(user.id).set(user)
                    deleteOldPicture(oldPictureUrl)
                }
            }
        }
    }

    //deletes the old picture when a user changes it to save space in storage
    private fun deleteOldPicture(url: String) {
        var name = url.substring(url.indexOf("%2F") + 3, (url.indexOf('?')))
        name = name.replace("%20"," ")
        storageRef.child(name).delete()
    }

}