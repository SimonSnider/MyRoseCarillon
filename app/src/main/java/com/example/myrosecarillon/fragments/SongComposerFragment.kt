package com.example.myrosecarillon.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myrosecarillon.R
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.midiEditor.MidiStructure
import com.example.myrosecarillon.midiEditor.MidiUploader
import com.example.myrosecarillon.objects.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_file_uploader.view.*
import kotlinx.android.synthetic.main.fragment_file_uploader.view.compose_button
import kotlinx.android.synthetic.main.fragment_file_uploader.view.composer_view
import kotlinx.android.synthetic.main.fragment_song_composer.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SongComposerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongComposerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_composer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.whole_note_button.setOnClickListener {
            view.composer_view.setPointerNote(MidiStructure.WHOLE_NOTE)
        }
        view.half_note_button.setOnClickListener {
            view.composer_view.setPointerNote(MidiStructure.HALF_NOTE)
        }
        view.quarter_note_button.setOnClickListener {
            view.composer_view.setPointerNote(MidiStructure.QUARTER_NOTE)
        }
        view.eighth_note_button.setOnClickListener {
            view.composer_view.setPointerNote(MidiStructure.EIGHTH_NOTE)
        }

        view.compose_button.setOnClickListener {
            val uploader = MidiUploader()
            val midi = view.composer_view.getMidi()
            val name = view.composer_title_view.text.toString()
            if(midi != null) {
                uploader.storageAdd(midi, name)
                auth.currentUser?.uid?.let { it1 -> Constants.userRef.document(it1).get().addOnSuccessListener {
                    val user = User.fromSnapshot(it)
                    user.uploadSong()
                } }
                findNavController().navigate(R.id.action_songComposerFragment_to_mySongsFragment)
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> {
                findNavController().navigate(R.id.action_songComposerFragment_to_profilePageFragment2)
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SongComposerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SongComposerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}