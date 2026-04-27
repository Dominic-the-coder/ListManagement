// TodoDao.kt = Actions you can perform on the notebook
// - View all words     → getByStatus()
// - Add a word         → insert()
// - Update a word      → update()
// - Delete a word      → delete()

package com.example.listmanagement.database

import androidx.room.*
import com.example.listmanagement.model.TodoEntity

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_table WHERE status = :status")
    fun getByStatus(status: String): androidx.lifecycle.LiveData<List<TodoEntity>>

    @Insert
    suspend fun insert(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}