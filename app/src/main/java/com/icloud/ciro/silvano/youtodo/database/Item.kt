package com.example.youtodo.databaseUtilities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "item")
data class Item (
    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo(name = "name")
    val name : String,

    @ColumnInfo(name = "category")
    val category : String,

    @ColumnInfo(name = "deadline")
    val deadline : String
) : Parcelable