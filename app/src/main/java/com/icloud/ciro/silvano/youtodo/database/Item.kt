package com.icloud.ciro.silvano.youtodo.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
/*Classe Entity:
* Rappresenta la tabella "item" del database
*/

@Parcelize
@Entity(tableName = "item", foreignKeys = [ForeignKey(entity=Category::class, parentColumns = ["name"], childColumns = ["category"], onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.NO_ACTION)], indices = [Index("category")])
data class Item (
    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo(name = "name")
    val name : String,

    @ColumnInfo(name = "category")
    val category : String,

    @ColumnInfo(name = "deadline")
    val deadline : String,

    @ColumnInfo(name = "isDone", defaultValue = "false")
    val isDone : Boolean
) : Parcelable