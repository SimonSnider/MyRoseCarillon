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

//    private fun createTopThreeSnapshotListener() {
//        topThree = ArrayList<Post>()
//        Log.d(Constants.TAG, "creating snapshot listener")
//        listenerRegistration = postRef.orderBy(Post.RATING_KEY, Query.Direction.DESCENDING).limit(3).addSnapshotListener{querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
//            if (e != null) {
//                Log.w(Constants.TAG, "listen error", e)
//            } else {
//                processSnapshotChanges(querySnapshot!!)
//            }
//        }
//    }

//    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
//        for(documentChange in querySnapshot.documentChanges){
//            val post = Post.fromSnapshot(documentChange.document)
//            when(documentChange.type){
//                DocumentChange.Type.ADDED -> {
//                    topThree.add(post)
//                }
//                DocumentChange.Type.MODIFIED -> {
//                    val index = topThree.indexOfFirst { it.id == post.id }
//                    topThree[index] = post
//                }
//                DocumentChange.Type.REMOVED -> {
//                    val index = topThree.indexOfFirst{it.id == post.id}
//                    topThree.removeAt(index)
//                }
//            }
//        }
//        updateView()
//    }
//
//    private fun updateView() {
//        Log.d(Constants.TAG, topThree.toString())
//    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MainMenuFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MainMenuFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}