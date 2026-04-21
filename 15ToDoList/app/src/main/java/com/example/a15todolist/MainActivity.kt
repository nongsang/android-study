package com.example.a15todolist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.a15todolist.databinding.ActivityMainBinding
import com.example.a15todolist.db.AppDatabase
import com.example.a15todolist.db.ToDoDao
import com.example.a15todolist.db.ToDoEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var db: AppDatabase
    private lateinit var toDoDao: ToDoDao
    private lateinit var toDoList: ArrayList<ToDoEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddTodo.setOnClickListener {
            val intent = Intent(this, AddToDoActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getInstance(this)!!
        toDoDao = db.getToDoDao()

        getAllToDoList()
    }

    private fun getAllToDoList() {
        {
            Thread {
                toDoList = ArrayList(toDoDao.getAll())
                setRecyclerView()
            }.start()
        }

        lifecycleScope.launch {
            toDoList = ArrayList(toDoDao.getAll())
            setRecyclerView()
        }
    }

    private fun setRecyclerView() {

    }
}