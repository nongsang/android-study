package com.example.a15todolist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 이 클래스는 엔티티의 배열이고, 버전은 1
@Database(entities = [ToDoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getToDoDao() : ToDoDao

    // 싱글톤 패턴으로 구현
    companion object {
        const val databaseName = "db_todo"
        var appDataBase: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase? {
            if (appDataBase == null) {
                appDataBase = Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()
            }

            return appDataBase
        }
    }
}