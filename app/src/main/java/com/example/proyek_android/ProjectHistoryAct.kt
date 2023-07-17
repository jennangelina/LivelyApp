package com.example.proyek_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.adapter.adapterProject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProjectHistoryAct : AppCompatActivity() {

    private lateinit var _rvProjectHistory: RecyclerView
    private var arProjectHistory =arrayListOf<project>()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_history)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!
        _rvProjectHistory = findViewById(R.id.rvProjectHistory)

        DisplayProjectData()
    }

    private fun DisplayProjectData() {
        _rvProjectHistory.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterProject(arProjectHistory)
        _rvProjectHistory.adapter = adapterP

        // read from firestore
        db.collection("projects").document(fb_user.uid).collection("myProjects")
            .get()
            .addOnSuccessListener { result ->
                arProjectHistory.clear()
                for(document in result){
                    val dataProject = project( document.id,
                        document.data.get("project_title").toString(),
                        document.data.get("project_status").toString(),
                        document.data.get("deadline_day").toString(),
                        document.data.get("deadline_month").toString(),
                        document.data.get("deadline_year").toString(),
                        document.data.get("project_ended").toString().toInt())
                    arProjectHistory.add(dataProject)
                }
                adapterP.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }
}