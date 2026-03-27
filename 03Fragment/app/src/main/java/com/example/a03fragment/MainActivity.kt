package com.example.a03fragment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a03fragment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRedFragment.setOnClickListener {
            // 프래그먼트 트랜잭션 객체 생성
            // supportFragmentManager은 프래그먼트 관련 작업 수행하는 매니저
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            // fragmentFrame의 레이아웃을 RedFragment 객체로 설정
            fragmentTransaction.replace(binding.fragmentFrame.id, RedFragment())
            // 트랜잭션 설정을 마친 이후 실제 적용
            fragmentTransaction.commit()
        }

        binding.btnBlueFragment.setOnClickListener {
            // 프래그먼트 트랜잭션 객체 생성
            // supportFragmentManager은 프래그먼트 관련 작업 수행하는 매니저
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            // fragmentFrame의 레이아웃을 BlueFragment 객체로 설정
            fragmentTransaction.replace(binding.fragmentFrame.id, BlueFragment())
            // 트랜잭션 설정을 마친 이후 실제 적용
            fragmentTransaction.commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}