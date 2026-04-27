// TodoEntity.kt = Structure of each page
// Word page format:
// - ID
// - Title
// - Meaning
// - Synonyms
// - Details
// - Status → "NEW" or "DONE"

package com.example.listmanagement.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val meaning: String? = null,
    val synonyms: String? = null,
    val details: String,
    val status: String // "NEW" or "DONE"
)