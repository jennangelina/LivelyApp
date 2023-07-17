package com.example.proyek_android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Articles(
    var article_title: String,
    var article_photo: String,
    var article_date: String,
    var article_category: String,
    var article_content : String
):Parcelable
