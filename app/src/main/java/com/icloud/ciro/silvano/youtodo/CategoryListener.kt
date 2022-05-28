package com.icloud.ciro.silvano.youtodo

import com.icloud.ciro.silvano.youtodo.database.Category

interface CategoryListener  {
    fun categoryEdit(category: Category)
    fun categoryDelete(category: Category)
}