package com.example.a15todolist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a15todolist.databinding.ActivityMainBinding
import com.example.a15todolist.db.AppDatabase
import com.example.a15todolist.db.ToDoDao
import com.example.a15todolist.db.ToDoEntity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var db: AppDatabase
    private lateinit var toDoDao: ToDoDao
    private lateinit var toDoList: ArrayList<ToDoEntity>
    private lateinit var adapter: ToDoRecyclerViewAdapter

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

        // 스레드를 직접 생성해서 사용하는 방법은 위험하므로 하지 않는다.
        /*Thread {
            toDoList = ArrayList(toDoDao.getAll())
            setRecyclerView()
        }.start()*/

        // lifecycleScope는 기본 디스패처가 Main이다.
        lifecycleScope.launch {
            toDoList = withContext(Dispatchers.IO) {
                ArrayList(toDoDao.getAll())
            }

            setRecyclerView()
        }
    }

    private fun setRecyclerView() {
        // 어댑터 생성
        adapter = ToDoRecyclerViewAdapter(toDoList)
        // 뷰 객체의 어댑터에 생성한 어댑터 설정
        binding.recyclerView.adapter = adapter
        // 레이아웃 매니저 생성 및 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onRestart() {
        super.onRestart()
        getAllToDoList()
    }
}