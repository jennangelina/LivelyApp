package com.example.proyek_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.adapter.adapterTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class TaskHistoryAct : AppCompatActivity() {

    private lateinit var _rvTaskHistory: RecyclerView
    private var arTaskHistory = arrayListOf<task>()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_history)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!
        _rvTaskHistory = findViewById(R.id.rvProjectHistory)

        DisplayTaskData()
    }

    private fun DisplayTaskData() {
        _rvTaskHistory.layoutManager = LinearLayoutManager(this)
        val adapterT = adapterTask(arTaskHistory)
        _rvTaskHistory.adapter = adapterT

        // read from firestore
        db.collection("tasks").document(fb_user.uid).collection("myTasks")
            .get()
            .addOnSuccessListener { result ->
                arTaskHistory.clear()
                for(document in result){
                    val dataTask = task(document.id, document.data.get("task_title").toString(), document.data.get("task_done").toString().toInt())
                    arTaskHistory.add(dataTask)
                }
                adapterT.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }

    }
}