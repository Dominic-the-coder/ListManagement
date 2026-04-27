package com.example.listmanagement.model

import java.io.Serializable
import java.sql.Date

data class TodoDetails (
    val id: Double,
    val title: String,
    val meaning: String? = null,
    val synonyms: String? = null,
    val details: String,
    val status: ToDoStatus,
    val date: Date? = null
) : Serializable

enum class ToDoStatus {
    NEW,
    DONE,
}