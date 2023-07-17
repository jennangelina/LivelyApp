package com.example.proyek_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class article_detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        val _tvNama = findViewById<TextView>(R.id.judulArticledet)
        val _tvDetail = findViewById<TextView>(R.id.contentArticledet)
        val _imgFoto = findViewById<ImageView>(R.id.fotoArticledet)

        val dataIntent = intent.getParcelableExtra<Articles>("articles")
        //_imgFoto.setImageResource(dataIntent!!.foto)
        val context = this
//        val imageRes = context.resources.getIdentifier(
//            dataIntent?.article_photo,"drawable", context.packageName)
//        Picasso.get().load(imageRes).into(_imgFoto)

        _tvNama.setText(dataIntent!!.article_title)
        _tvDetail.setText(dataIntent!!.article_content)
    }
}