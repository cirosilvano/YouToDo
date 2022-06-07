package com.icloud.ciro.silvano.youtodo.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import kotlinx.parcelize.Parcelize

/*Classe Entity:
* Rappresenta la tabella "card" del database
*/
@Parcelize
@Entity(tableName = "card",
    foreignKeys = [
        ForeignKey(entity=Category::class,
        parentColumns = ["name"],
        childColumns = ["category"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE)]
    )
data class Card (
    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo(name = "name")
    val name : String,

    @ColumnInfo(name = "category", defaultValue = "default")
    val category : String,

    @ColumnInfo(name = "deadline")
    val deadline : String,

    @ColumnInfo(name = "isDone", defaultValue = "false")
    val isDone : Boolean
) : Parcelable