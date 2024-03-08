package com.dea.mentalcare.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class HistoryItem(
    val id: String,
    val name: String,
    val age: String,
    val gender: String,
    val status: String,
    val maritalStatus: String,
    val result: String,
    val message: String
) : Parcelable