package com.example.proyek_android.adapter

import android.content.Context
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayoutStates
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyek_android.Articles
import com.example.proyek_android.R
import com.squareup.picasso.Picasso

class adapterArticle (
    private val listArticle: ArrayList<Articles>
) : RecyclerView.Adapter<adapterArticle.ListViewHolder>() {
    private lateinit var onItemClickCallback : OnItemClickCallBack

    private lateinit var context: Context

    interface OnItemClickCallBack {
        fun onItemClicked(data: Articles)
    }

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallback = onItemClickCallBack
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var _judulArticle: TextView = itemView.findViewById(R.id.judul_article)
        var _fotoArticle: ImageView = itemView.findViewById(R.id.article_image)
        var _dateArticle: TextView = itemView.findViewById(R.id.article_date)
        var _categoryArticle: TextView = itemView.findViewById(R.id.article_category)
        var _cvArticle: CardView = itemView.findViewById((R.id.cvArticle))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.itemarticle, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var Articles = listArticle[position]

        holder._judulArticle.setText(Articles.article_title)
        holder._dateArticle.setText(Articles.article_date)
        holder._categoryArticle.setText(Articles.article_category)

        Picasso.get()
            .load(Articles.article_photo)
            .centerCrop()
            .into(holder._fotoArticle)

        holder._cvArticle.setOnClickListener{
            onItemClickCallback.onItemClicked(listArticle[position])
        }
    }

    override fun getItemCount(): Int {
        return listArticle.count()
    }
}