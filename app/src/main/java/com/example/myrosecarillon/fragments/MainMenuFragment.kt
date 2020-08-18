package com.example.myrosecarillon.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myrosecarillon.R
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_main_menu.view.*
import org.w3c.dom.Document


class MainMenuFragment : Fragment() {

    private val postRef = FirebaseFirestore.getInstance().collection(Constants.POSTS_PATH)
    private var topThree = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTopThree()
        view.compose_button.setOnClickListener { findNavController().navigate(R.id.action_mainMenuFragment_to_songComposerFragment) }
        view.upload_button.setOnClickListener { findNavController().navigate(R.id.action_mainMenuFragment_to_fileUploaderFragment) }
        view.view_all_button.setOnClickListener { findNavController().navigate(R.id.action_mainMenuFragment_to_songBoardFragment) }
        view.my_songs_nav_button.setOnClickListener { findNavController().navigate(R.id.action_mainMenuFragment_to_mySongsFragment) }
    }

    private fun getTopThree() {
        topThree = ArrayList<Post>()
        postRef.orderBy(Post.RATING_KEY, Query.Direction.DESCENDING).limit(3).get().addOnSuccessListener {querySnapshot ->
            for (snapshot in querySnapshot){
                val post = Post.fromSnapshot(snapshot)
                post.songRef?.get()?.addOnCompleteListener() {task ->
                    post.song = task.result?.let { it1 -> Song.fromSnapshot(it1) }
                    val index = topThree.indexOfFirst {it.id == post.id }
                    updateView(index)
                }
                topThree.add(post)
            }
            Log.d(Constants.TAG, topThree.toString())
        }
    }

    private fun updateView(index: Int) {
        val post = topThree[index]
        when(index){
            0 -> {
                view?.card_title?.text = post.song?.title
                view?.rating?.text = post.rating.toString()
                post.song?.midi?.let { view?.now_playing_score?.sendMidi(it) }
            }
            1 -> {
                view?.up_next_song_name?.text = post.song?.title
                view?.rating_1?.text = post.rating.toString()
                post.song?.midi?.let { view?.up_next_score?.sendMidi(it) }
            }
            2 -> {
                view?.future_song_name?.text = post.song?.title
                view?.rating_2?.text = post.rating.toString()
                post.song?.midi?.let { view?.future_score?.sendMidi(it) }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                Log.d(Constants.TAG, "Navigating to profile page")
                findNavController().navigate(R.id.action_mainMenuFragment_to_profileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}