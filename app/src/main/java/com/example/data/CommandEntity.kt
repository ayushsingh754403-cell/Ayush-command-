package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_commands")
data class CommandEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val query: String,
    val title: String,
    val description: String,
    val commandsText: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
