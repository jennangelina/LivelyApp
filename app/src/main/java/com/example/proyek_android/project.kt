package com.example.proyek_android

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class project(
    var project_id: String,
    var project_title: String,
    var project_status: String,
    var deadline_day: String,
    var deadline_month: String,
    var deadline_year: String,
    var project_ended: Int
): Parcelable
