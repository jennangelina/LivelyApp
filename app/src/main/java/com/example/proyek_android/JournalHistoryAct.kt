package com.example.proyek_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.adapter.adapterJournal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class JournalHistoryAct : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    private lateinit var _rvJournal: RecyclerView
    private var arJournal = arrayListOf<journal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_history)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!
        _rvJournal = findViewById(R.id.rvJournal)

        DisplayJournalData()
    }

    private fun DisplayJournalData() {
        _rvJournal.layoutManager = LinearLayoutManager(this)
        val adapterJ = adapterJournal(arJournal)
        _rvJournal.adapter = adapterJ

        adapterJ.setOnItemClickCallback(object: adapterJournal.OnItemClickCallback {
            override fun onItemClicked(data: journal) {
                val intent = Intent(this@JournalHistoryAct, DetailJournalAct::class.java)
                intent.putExtra("journalData", data)
                startActivity(intent)
            }
        })

        // read from firestore
        db.collection("journals").document(fb_user.uid).collection("myJournals")
            .get()
            .addOnSuccessListener { result ->
                arJournal.clear()
                for(document in result) {
                    val dataJournal = journal(document.data.get("journal_title").toString(),
                    document.data.get("journal_content").toString(),
                        document.data.get("journal_mood").toString(),
                    document.data.get("journal_day").toString(),
                        document.data.get("journal_month").toString(),
                        document.data.get("journal_year").toString())
                    arJournal.add(dataJournal)
                }
                adapterJ.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }
}