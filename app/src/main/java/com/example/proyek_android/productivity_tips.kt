package com.example.proyek_android

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.adapter.adapterArticle
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class productivity_tips : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var sp: SharedPreferences
    private var arArticle = arrayListOf<Articles>()
    private lateinit var rvArticle : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productivity_tips)

        sp = getSharedPreferences("dataSP", MODE_PRIVATE)
        db = FirebaseFirestore.getInstance()
        rvArticle = findViewById(R.id.rvProductivity)
        auth = FirebaseAuth.getInstance()

        TampilkanData()
    }

    private fun TampilkanData() {
        rvArticle.layoutManager = LinearLayoutManager(this)
        val adapterArt = adapterArticle(arArticle)
        rvArticle.adapter = adapterArt
        db.collection("articles")
            .whereEqualTo("article_category","Productivity Tips")
            .get()
            .addOnSuccessListener { show ->
                arArticle.clear()
                for(document in show){
                    val dataArticle= Articles(document.data.get("article_title").toString(), document.data.get("article_photo").toString(),document.data.get("article_date").toString(),document.data.get("article_category").toString(),document.data.get("article_content").toString())
                    arArticle.add(dataArticle)
                }
                adapterArt.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }

        rvArticle.adapter = adapterArt
        adapterArt.setOnItemClickCallback(object: adapterArticle.OnItemClickCallBack {
            override fun onItemClicked(data: Articles){
                Toast.makeText(this@productivity_tips, data.article_title, Toast.LENGTH_LONG).show()
            }
        })
    }
}