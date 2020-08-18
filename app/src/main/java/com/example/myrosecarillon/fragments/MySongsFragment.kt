package com.example.myrosecarillon.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrosecarillon.MySongsAdapter
import com.example.myrosecarillon.R
import kotlinx.android.synthetic.main.fragment_my_songs.*


class MySongsFragment : Fragment() {
    lateinit var adapter: MySongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_my_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializes the adapter for the recycler view
        adapter = MySongsAdapter(requireContext())
        adapter.addSnapshotListener()
        my_songs_recycler_view.adapter = adapter
        my_songs_recycler_view.layoutManager  = LinearLayoutManager(requireContext())
    }

    //allows this fragment to navigate to the profile page
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> {
                findNavController().navigate(R.id.action_mySongsFragment_to_profilePageFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}