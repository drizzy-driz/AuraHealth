package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_terms")
data class BookmarkedTermEntity(
    @PrimaryKey
    val termId: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
