package com.icloud.ciro.silvano.youtodo

import com.icloud.ciro.silvano.youtodo.database.Card

interface OnItemSwipeListener {
    fun onCheckCardClick(card: Card)
    fun onCardSwipe(card: Card)
}