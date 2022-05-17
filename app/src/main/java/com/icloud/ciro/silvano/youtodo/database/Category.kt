package com.icloud.ciro.silvano.youtodo.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
/*Classe Entity:
* Rappresenta la tabella "item" del database
*/

@Parcelize
@Entity(tableName = "category")
data class Category (
    @PrimaryKey
    val name : String,

    @ColumnInfo(name = "description")
    val description : String?
) : Parcelable