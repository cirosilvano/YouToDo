package com.icloud.ciro.silvano.youtodo

import com.icloud.ciro.silvano.youtodo.database.Item

interface OnItemSwipeListener {
    fun onItemTouchCheck(item: Item)

    fun onItemSwipe(item: Item)
}