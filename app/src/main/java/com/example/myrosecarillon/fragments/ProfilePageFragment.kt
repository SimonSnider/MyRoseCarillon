package com.example.myrosecarillon.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.myrosecarillon.R
import com.example.myrosecarillon.constants.Constants
import com.example.myrosecarillon.objects.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_page.view.*


class ProfilePageFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
    private val userRef = FirebaseFirestore.getInstance().collection(Constants.USERS_PATH)
    private val auth = FirebaseAuth.getInstance()
    private lateinit var user: User

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
        val view = inflater.inflate(R.layout.fragment_profile_page, container, false)
        auth.currentUser?.uid?.let {
            userRef.document(it).get().addOnSuccessListener { snap ->
                user = User.fromSnapshot(snap)
                view.name_text_view.text = user.displayName
                view.carills_stat_text_view.text = String.format(requireContext().resources.getString(R.string.carills_var), user.carills)
                view.songs_played_stat_text_view.text = String.format(requireContext().resources.getString(R.string.songs_played_var), user.carills)
                view.songs_uploaded_stat_text_view.text = String.format(requireContext().resources.getString(R.string.songs_uploaded_var), user.carills)
                view.upvotes_given_stat_text_view.text = String.format(requireContext().resources.getString(R.string.upvotes_given_var), user.carills)
                Picasso.get().load(user.pictureUrl).into(view.findViewById<ImageView>(R.id.profile_imageView))
            }
        }
        view.my_songs_button.setOnClickListener{
            findNavController().navigate(R.id.action_profilePageFragment_to_mySongsFragment)
        }
        view.user_settings_button.setOnClickListener{
            //TODO: launch settings ui
        }
        return view
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment ProfilePageFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ProfilePageFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}