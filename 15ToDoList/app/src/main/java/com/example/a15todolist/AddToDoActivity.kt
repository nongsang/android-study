package com.example.a15todolist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.a15todolist.databinding.ActivityAddTodoBinding
import com.example.a15todolist.db.AppDatabase
import com.example.a15todolist.db.ToDoDao
import com.example.a15todolist.db.ToDoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.stackTrace

class AddToDoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTodoBinding
    private lateinit var db: AppDatabase
    private lateinit var toDoDao: ToDoDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        toDoDao = db.getToDoDao()

        binding.btnCompletion.setOnClickListener {
            insertToDo()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun insertToDo() {
        // 할 일 제목
        val toDoTitle = binding.edtTitle.text.toString()
        // 중요도
        var toDoImportance = binding.radioGroup.checkedRadioButtonId

        toDoImportance = when(toDoImportance) {
            R.id.btn_high -> {
                1
            }

            R.id.btn_middle -> {
                2
            }

            R.id.btn_low -> {
                3
            }

            else -> {
                -1
            }
        }

        if (toDoImportance == -1 || toDoTitle.isBlank()) {
            Toast.makeText(this, "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
        } else {
            {
                // 데이터베이스 관련 작업은 백그라운드 쓰레드에서 진행해야 한다.
                // 네트워크 통신, 데이터베이스 쿼리 등은 처리에 긴 시간이 들기 때문
                Thread {
                    // 데이터베이스에 엔티티 저장
                    toDoDao.insertToDo(ToDoEntity(null, toDoTitle, toDoImportance))
                    // 액티비티 관련 작업은 UI 쓰레드에서 처리하도록 설정
                    runOnUiThread {
                        Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()    // AddToDoActivity 종료
                    }
                }.start()
            }

            // 코루틴을 사용하여 데이터베이스 처리
            // 네트워크 통신, 데이터베이스 쿼리 등은 처리에 긴 시간이 들기 때문
            lifecycleScope.launch {
                try {
                    // 네트워크 통신, 데이터베이스 쿼리 등에 특화된 스레드풀 사용
                    withContext(Dispatchers.IO) {
                        toDoDao.insertToDo(ToDoEntity(null, toDoTitle, toDoImportance))
                    }

                    // lifecycleScope 코루틴을 사용하면 자동으로 생명주기에 맞게 동기화를 해주므로 runOnUiThread를 사용하지 않아도 된다.
                    Toast.makeText(this@AddToDoActivity, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@AddToDoActivity, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}