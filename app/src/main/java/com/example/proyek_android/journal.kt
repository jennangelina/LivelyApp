package com.example.proyek_android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class journal(
    var journal_title: String,
    var journal_content: String,
    var journal_mood: String,
    var journal_day: String,
    var journal_month:String,
    var journal_year: String
): Parcelable
