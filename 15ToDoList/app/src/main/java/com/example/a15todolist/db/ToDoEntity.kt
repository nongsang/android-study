package com.example.a15todolist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ToDoEntity(
    @PrimaryKey(autoGenerate = true) var id : Int? = null,  // 기본키는 무조건 있어야 한다.
    @ColumnInfo(name="title") var title : String,
    @ColumnInfo(name="importance") var importance : Int
)
