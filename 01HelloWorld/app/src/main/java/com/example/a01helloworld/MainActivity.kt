package com.example.a01helloworld

import android.R.attr.text
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a01helloworld.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 보여줄 레이아웃 지정
        // 자동 생성된 R 클래스에 있는 active_main.xml을 레이아웃으로 지정
        // 뷰 바인딩 기능을 사용한다면 바인딩 객체의 루트를 레이이웃으로 지정하면 된다.
        // setContentView(R.layout.activity_main)
        // R.layout.activity_main를 나타내는 ActivityMainBinding의 바인딩 객체 획득
        val binding = ActivityMainBinding.inflate(layoutInflater)

        // 바인딩 객체의 루트를 레이아웃으로 지정
        setContentView(binding.root)

        // 동작 정의
        binding.btnHello.setOnClickListener {
            binding.txtSay.text = "Hello!"
        }

        binding.btnWorld.setOnClickListener {
            binding.txtSay.text = "World!"
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}