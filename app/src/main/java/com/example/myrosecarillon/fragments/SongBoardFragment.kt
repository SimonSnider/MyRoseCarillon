package com.example.myrosecarillon.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrosecarillon.R
import com.example.myrosecarillon.SongBoardAdapter
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.Post
import com.example.myrosecarillon.objects.Song
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.choose_song_dialog.view.*
import kotlinx.android.synthetic.main.fragment_song_board.*
import java.time.LocalDateTime


class SongBoardFragment : Fragment() {

    lateinit var adapter: SongBoardAdapter
    private var songsRef = Constants.songsRef
    private var postRef = Constants.postsRef
    private var auth = FirebaseAuth.getInstance()
    private var userRef = Constants.userRef

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializes the adapter for the queue
        adapter = SongBoardAdapter(requireContext())
        adapter.addSnapshotListener()
        song_board_recycler_view.adapter = adapter
        song_board_recycler_view.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.action_add_post).isVisible = true
    }

    //allows this fragment to navigate to the profile page and add posts
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add_post -> {
                showChooseSongDialog()
                true
            }
            R.id.action_profile -> {
                findNavController().navigate(R.id.action_songBoardFragment_to_profilePageFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //shows the dialog to add a new post
    private fun showChooseSongDialog(){
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        var selectedSong: Song? = null

        builder.setTitle(requireContext().resources.getString(R.string.choose_song))
        val view = LayoutInflater.from(context).inflate(R.layout.choose_song_dialog, null, false)
        builder.setView(view)

        //gets the songs created by the current user to populate a spinner
        songsRef.whereEqualTo("creatorID", auth.currentUser?.uid).get().addOnSuccessListener {documents ->
            val songs = documents.map {Song.fromSnapshot(it)}
            val songTitles = songs.map {it.title}

            val spinner = view.choose_song_spinner
            spinner.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, songTitles)

            //shows the selected song's data in the card view in the dialog
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view2: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSong = songs[position]
                    view.card_title.text = selectedSong!!.title
                    view.spinner_display.resetMidi()
                    view.spinner_display.sendMidi(selectedSong!!.midi)
                    view.play_song_demo.setOnClickListener { view.spinner_display.play() }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //do nothing
                }

            }
        }

        //checks if the user has already posted the same song, if not it adds the new post to the database
        builder.setPositiveButton(android.R.string.ok) {_, _ ->
            if (adapter.checkForSong(selectedSong?.id)){
                Toast.makeText(context, "You have already posted that song", Toast.LENGTH_LONG).show()
            } else postRef.add(Post(date = Timestamp.now(), userRef = userRef.document(auth.currentUser!!.uid), songRef = songsRef.document(selectedSong!!.id)))
        }
        builder.setNegativeButton(android.R.string.cancel) {_, _ ->}

        builder.create().show()
    }

}