package com.example.proyek_android

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFr.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFr : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var db : FirebaseFirestore

    lateinit var _tv_name : TextView
    lateinit var _iv_profile : ImageView
    lateinit var _tv_email : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val _btn_logout = view.findViewById<Button>(R.id.btn_logout)
        val _btn_editprofile = view.findViewById<Button>(R.id.btn_editprofile)
        val _clTaskHistory = view.findViewById<ConstraintLayout>(R.id.clTaskHistory)
        val _clProjectHistory = view.findViewById<ConstraintLayout>(R.id.clProjectHistory)

        _tv_name = view.findViewById(R.id.tv_name)
        _tv_email = view.findViewById(R.id.tv_email)
        _iv_profile = view.findViewById(R.id.iv_profile)

        RefreshData()

        _btn_editprofile.setOnClickListener{
            Intent(activity, EditProfileAct::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        _clTaskHistory.setOnClickListener {
            Intent(activity, TaskHistoryAct::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        _clProjectHistory.setOnClickListener {
            Intent(activity, ProjectHistoryAct::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        _btn_logout.setOnClickListener{
            auth.signOut()
            Intent(activity, MainActivity::class.java).also{
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun RefreshData(){
        val userUID = auth.uid
        db.collection("tbUser").document(userUID.toString()).get()
            .addOnSuccessListener {
                _tv_name.setText(it.data?.get("nama").toString())
                _tv_email.setText(it.data?.get("email").toString())
                if(it.data?.get("gender").toString() == "male"){
                    _iv_profile.setImageResource(R.drawable.profile_picture_male)
                }
                else if(it.data?.get("gender").toString() == "female"){
                    _iv_profile.setImageResource(R.drawable.profile_picture_female)
                }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFr().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}