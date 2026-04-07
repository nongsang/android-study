package com.example.a12stopwatch

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
import kotlin.concurrent.timer

// 클릭 이벤트 처리 인터페이스 View.OnClickListener 추가
class MainActivity : AppCompatActivity(), View.OnClickListener {
    var timer: Timer? = null    // java.util.Timer
    var isRunning = false       // 타이머가 실행중인지 여부
    var time = 0L;              // time 변수 추가

    private lateinit var btn_start: Button
    private lateinit var btn_refresh: Button
    private lateinit var tv_minute: TextView
    private lateinit var tv_second: TextView
    private lateinit var tv_millisecond: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 뷰 가져오기
        btn_start = findViewById(R.id.btn_start)
        btn_refresh = findViewById(R.id.btn_refresh)
        tv_minute = findViewById(R.id.tv_minute)
        tv_second = findViewById(R.id.tv_second)
        tv_millisecond = findViewById(R.id.tv_millisecond)

        // 클릭 이벤트 등록
        btn_start.setOnClickListener(this)
        btn_refresh.setOnClickListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 클릭 이벤트 처리
    override fun onClick(v: View?) {
        when(v?.id) {               // 입력이 발생한 뷰의 id가
            R.id.btn_start -> {     // start 일 때
                if (isRunning) {    // 타이머가 동작하고 있으면
                    pause()         // pause 함수 실행
                } else {            // 타이머가 동작하고 있지 않으면
                    start()         // start 함수 실행
                }
            }
            R.id.btn_refresh -> {   // 뷰가 refresh 일 때
                refresh()           // refresh 함수 실행
            }
        }
    }

    private fun start() {
        btn_start.text = getString(R.string.pause)
        btn_start.setBackgroundColor(getColor(R.color.red))
        isRunning = true

        // 기존의 타이머 삭제
        timer = null

        // 10 밀리초 단위로 실행되는 타이머 생성
        timer = timer(period = 10) {
            // 10 밀리초 단위로 하나씩 올리기
            time++

            // 시간 계산
            val milli_second = time % 100
            val second = (time % 6000) / 100
            val minute = time / 6000

            // UI 업데이트 타이밍 때 백그라운드에서 실행되던 데이터로 UI 업데이트
            runOnUiThread {
                if (isRunning) {
                    tv_millisecond.text =
                        if (milli_second < 10)
                            ".0$milli_second"
                        else
                            ".$milli_second"

                    tv_second.text =
                        if (second < 10)
                            ":0$second"
                        else
                            ":$second"

                    tv_minute.text =
                        if (minute < 10)
                            "0$minute"
                        else
                            "$minute"
                }
            }
        }
    }

    private fun pause() {
        btn_start.text = getString(R.string.start)
        btn_start.setBackgroundColor(getColor(R.color.blue))

        isRunning = false
        timer?.cancel()
    }

    private fun refresh() {
        timer?.cancel()

        btn_start.text = getString(R.string.start)
        btn_start.setBackgroundColor(getColor(R.color.blue))
        isRunning = false

        time = 0
        tv_millisecond.text = ".00"
        tv_second.text = ":00"
        tv_minute.text = "00"
    }
}