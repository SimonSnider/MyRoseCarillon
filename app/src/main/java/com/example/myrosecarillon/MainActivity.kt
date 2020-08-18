package com.example.myrosecarillon

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.fragments.MainMenuFragment
import com.example.myrosecarillon.objects.User
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.rosefire.Rosefire
import edu.rosehulman.rosefire.RosefireResult
import kotlinx.android.synthetic.main.content_main.*
import java.io.FileInputStream


class MainActivity : AppCompatActivity(){

    private val userRef = Constants.userRef
    private val auth = FirebaseAuth.getInstance()
    private var authFlag = false
    lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private val RC_ROSEFIRE_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false);

        //sets the main menu button at the top of the screen to take the user to the main menu
        (findViewById<Button>(R.id.toolbar_title))?.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.mainMenuFragment)
        }

        initializeListeners()
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        //hide the add post action
        menu.findItem(R.id.action_add_post).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                authFlag = false
                auth.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun initializeListeners() {
        //initialize authStateListener to handle user login
        authStateListener = FirebaseAuth.AuthStateListener {auth: FirebaseAuth ->
            val user = auth.currentUser
            Log.d(Constants.TAG, "Auth Listener: user = $user")
            if (user != null){
                Log.d(Constants.TAG, "UID = ${user.uid}")
                userRef.document(user.uid).get().addOnSuccessListener {
                    if(!it.exists()){
                        //if this is a new user, add a user object to the database for them
                        Log.d(Constants.TAG, "Adding new user for first login")
                        userRef.document(user.uid).set(User(Constants.DEFAULT_PICTURE_PATH, user.uid, 0, false))
                    }
                }
            } else if (!authFlag) {
                //auth flag is used to prevent login screen from launching twice
                authFlag = true
                launchLoginUI()
            }
        }
    }


    //creates intent to open RoseFire login and launches the intent
    private fun launchLoginUI() {
        val signInIntent: Intent = Rosefire.getSignInIntent(this, getString(R.string.rosefire_key))
        startActivityForResult(signInIntent, RC_ROSEFIRE_SIGN_IN)
    }

    //catches the result of the RoseFire sign in intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_ROSEFIRE_SIGN_IN){
            val result: RosefireResult = Rosefire.getSignInResultFromIntent(data)
            if (!result.isSuccessful){
                Log.d(Constants.TAG, "The user cancelled the login")
                authFlag = false
                return
            }
            //if the task is not successful, send a toast, otherwise let the authStateListener do its function
            auth.signInWithCustomToken(result.token).addOnCompleteListener {task ->
                Log.d(Constants.TAG, "signInWithCustomToken:onComplete:" + task.isSuccessful)
                if (!task.isSuccessful) {
                    Log.w(Constants.TAG, "signInWithCustomToken", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    authFlag = false
                } else authFlag = true
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}