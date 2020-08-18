package com.example.test.mc.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val name: String,
    val url: String,
    val thumbUrl: String
): Parcelable