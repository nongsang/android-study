package com.example.a15todolist.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ToDoDao {
    @Query("SELECT * FROM ToDoEntity")
    fun getAll() : List<ToDoEntity>     // 모든 데이터 가져오기

    @Insert
    fun insertToDo(toDo : ToDoEntity)   // 데이터 넣기

    @Delete
    fun deleteToDo(toDo: ToDoEntity)    // 데이터 삭제
}