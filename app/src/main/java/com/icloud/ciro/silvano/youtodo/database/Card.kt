package com.icloud.ciro.silvano.youtodo.database

import android.os.Parcelable
import androidx.room.*
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
        onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["category"])]
    )
data class Card (
    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo(name = "name")
    val name : String,

    @ColumnInfo(name = "category")
    val category : String,

    @ColumnInfo(name = "deadline")
    val deadline : String,

    @ColumnInfo(name = "isDone")
    val isDone : Boolean
) : Parcelable